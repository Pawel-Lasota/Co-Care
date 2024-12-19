package com.example.icsp.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.icsp.R;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * AddMedicineStorageActvity Activity class
 * <p>
 * This class is utilised for the medicine storage activity within the homepage dashboard. This is the place to save information such as the medicine quantity and
 * any required notes, e.g. whether the medicine requires a prescription or not
 */

public class AddMedicineStorageActvity extends AppCompatActivity {
    private EditText medicineName, medicineQuantity, medicineNotes;
    private Button saveBtn;
    private ImageButton backBtn;
    private String medicineStorageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine_storage);

        //Fetching required variables from layout
        medicineName = findViewById(R.id.medicineNameEditText);
        medicineQuantity = findViewById(R.id.medicineQuantityEditText);
        medicineNotes = findViewById(R.id.notesEditText);
        backBtn = findViewById(R.id.back_btn);
        saveBtn = findViewById(R.id.addFB);

        backBtn.setOnClickListener(view -> onBackPressed());

        //Retrieving medicineId so current information is loaded for existing medicines
        medicineStorageId = getIntent().getStringExtra("medicineStorageId");

        if (medicineStorageId != null && !medicineStorageId.isEmpty()) {
            loadMedicineData(medicineStorageId);
        }

        //Save button so if the medicine id is not null then an existing medicine is updated but if not then a new medicine is added to firebase
        saveBtn.setOnClickListener(view -> {
            if (medicineStorageId != null) {
                updateMedicineInStorage();
            } else {
                addMedicineToStorageFirestore();
            }
        });
    }

    //Editing and updating an existing medicine
    private void updateMedicineInStorage() {
        String updatedName = medicineName.getText().toString();
        String updatedQuantity = medicineQuantity.getText().toString();
        String updatedNotes = medicineNotes.getText().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", updatedName);
        updatedData.put("quantityLeft", updatedQuantity);
        updatedData.put("notes", updatedNotes);

        FirebaseUtil.getMedicineStorageCollectionReference().document(medicineStorageId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    AndroidUtil.showToast(AddMedicineStorageActvity.this, "Medicine updated successfully");
                    //Upon success, the medicine storage activity opens
                    Intent intent = new Intent(AddMedicineStorageActvity.this, MedicineStorageActivity.class);
                    startActivity(intent);
                    finish();
                })
                //Error Handling
                .addOnFailureListener(e -> {
                    AndroidUtil.showToast(AddMedicineStorageActvity.this, "Error updating medicine: " + e.getMessage());
                    Log.e("AddMedicineActivity", "Error updating medicine", e);
                });
    }

    //Loading medicine data for existing medicines
    private void loadMedicineData(String medicineStorageId) {
        FirebaseUtil.getMedicineStorageCollectionReference().document(medicineStorageId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MedicineStorageModel storage = documentSnapshot.toObject(MedicineStorageModel.class);
                        medicineName.setText(storage.getName());
                        medicineQuantity.setText(storage.getQuantityLeft());
                        medicineNotes.setText(storage.getNotes());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddMedicineActivity", "Error loading data", e);
                });
    }

    //Function for adding new medicines to storage via firebase
    private void addMedicineToStorageFirestore() {
        String name = medicineName.getText().toString();
        String quantity = medicineQuantity.getText().toString();
        String notes = medicineNotes.getText().toString();

        //Data Validation - ensuring that fields aren't empty
        boolean hasError = false;
        if (name.isEmpty()) {
            medicineName.setError("Medicine Name cannot be empty");
            hasError = true;
        }
        if (quantity.isEmpty()) {
            medicineQuantity.setError("Quantity cannot be empty");
            hasError = true;
        }
        if (hasError) {
            return;
        }

        //Ensuring current user ID is not null as it will also be added as a value in the medicine document. (Not necessary currently but maybe with future functionality)
        String userId = FirebaseUtil.currentUserId();
        if (userId == null || userId.isEmpty()) {
            Log.e("AddMedicineActivity", "User ID is null or empty");
            return;
        }
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("userId", userId);
        recipient.put("name", name);
        recipient.put("quantityLeft", quantity);
        recipient.put("notes", notes);
        FirebaseUtil.getMedicineStorageCollectionReference()
                .add(recipient)
                .addOnSuccessListener(documentReference -> {
                    AndroidUtil.showToast(AddMedicineStorageActvity.this, "Medicine added");
                    //On success open the medicine storage activity
                    Intent intent = new Intent(AddMedicineStorageActvity.this, MedicineStorageActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> AndroidUtil.showToast(AddMedicineStorageActvity.this, "Error adding medicine" + e.getMessage()));
    }
}
