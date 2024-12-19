package com.example.icsp.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.icsp.MainActivity;
import com.example.icsp.R;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * MedicineStorageActivity Activity Class
 * <p>
 * This class is responsible for the medicine storage - fetching & displaying all medicine storage details.
 */

public class MedicineStorageActivity extends AppCompatActivity {

    private ImageButton backBtn, addBtn;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_storage);

        //Retrieving required variables from layout
        backBtn = findViewById(R.id.back_btn);
        addBtn = findViewById(R.id.add_button);
        container = findViewById(R.id.medicine_storage_container);

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MedicineStorageActivity.this, MainActivity.class);
            startActivity(intent);
        });

        addBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MedicineStorageActivity.this, AddMedicineStorageActvity.class);
            startActivity(intent);
        });

        fetchAndDisplayStorage();
    }

    //This function is responsible for fetching all medicines for the storage from the medicineStorage document
    private void fetchAndDisplayStorage() {
        FirebaseUtil.getMedicineStorageCollectionReference().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                container.removeAllViews();
                for(QueryDocumentSnapshot document : task.getResult()){
                    MedicineStorageModel storage = document.toObject(MedicineStorageModel.class);
                    storage.setMedicineStorageId(document.getId());
                    View storageView = createStorageView(storage);
                    container.addView(storageView);
                }
            }
        });
    }

    private View createStorageView(MedicineStorageModel storage) {
        View view = LayoutInflater.from(this).inflate(R.layout.displaying_medicine_storage, container, false);

        //Retrieving more required variables from layout
        TextView medicineNameTextView = view.findViewById(R.id.medicineName);
        TextView medicineQuantityTextView = view.findViewById(R.id.medicineQuantity);
        TextView medicineNotesTextView = view.findViewById(R.id.medicineNotes);
        TextView editBtn = view.findViewById(R.id.medicine_storage_edit_btn);
        ImageButton removeBtn = view.findViewById(R.id.medicine_storage_remove_btn);

        //The edit text view navigates the user to the AddMedicineStorageActivity which displays the form with existing details
        editBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(MedicineStorageActivity.this, AddMedicineStorageActvity.class);
            intent.putExtra("medicineStorageId", storage.getMedicineStorageId());
            startActivity(intent);
        });

        //The removeBtn Button calls the removeStoredMedicine function to remove the medicine in the storage with a specific ID
        removeBtn.setOnClickListener(view1 -> {
            removeStoredMedicine(storage.getMedicineStorageId());
        });

        //Appropriately displaying the medicine storage information
        medicineNameTextView.setText(storage.getName());
        medicineQuantityTextView.setText("Quantity Left: " + storage.getQuantityLeft());
        medicineNotesTextView.setText("Notes: " + storage.getNotes());

        return view;
    }

    //This function is responsible for removing the medicine stored within the storage from firebase
    private void removeStoredMedicine(String medicineStorageId) {
        FirebaseUtil.getMedicineStorageCollectionReference().document(medicineStorageId)
                .delete()
                .addOnSuccessListener(aVoid -> fetchAndDisplayStorage())
                .addOnFailureListener(e -> AndroidUtil.showToast(MedicineStorageActivity.this, "Something Went Wrong"));
    }
}
