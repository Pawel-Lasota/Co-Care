package com.example.icsp.medicine;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.example.icsp.MainActivity;
import com.example.icsp.R;
import com.example.icsp.homepage.AddRecipientActivity;
import com.example.icsp.homepage.CareRecipientsActivity;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AddMedicineActivity Activity Class
 * <p>
 * This activity class is responsible for the add medicine form and adding new medicine schedules into firebase.
 * This is the activity that is launched upon clicking on the float action button in the medicine schedule fragment.
 */

public class AddMedicineActivity extends AppCompatActivity {

    private EditText timeEditText, medicineNameEditText, dosageEditText, requirementsEditText;
    private Map<String, String> recipientMap = new HashMap<>();
    private Button addMedicineButton;
    private ImageButton backBtn;
    private Spinner nameSpinner, daySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        FirebaseApp.initializeApp(this);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        addMedicineButton = findViewById(R.id.addFB);
        addMedicineButton.setOnClickListener(v -> {
            addMedicineToFirestore();
        });

        dosageEditText = findViewById(R.id.dosageEditText);
        dosageEditText.setOnClickListener(v -> {
            showDosageInputDialog();
        });

        timeEditText = findViewById(R.id.timeEditText);
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        nameSpinner = findViewById(R.id.nameSpinner);
        fetchRecipientNames();

        Spinner daySpinner = findViewById(R.id.daySpinner);
        //Retrieving days of week from the arrays file from the values package and putting it into the select day spinner
        String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
    }
    
    private void showTimePickerDialog(){
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    String formattedTime = String.format("%02d:%02d", hourOfDay, minute1);
                    timeEditText.setText(formattedTime);
                },hour,minute,true
        );

        timePickerDialog.show();

    }
    private void showDosageInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Dosage");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_dosage_input, null);

        final EditText dosageInputEditText = dialogView.findViewById(R.id.dosageInputEditText);
        final Spinner unitSpinner = dialogView.findViewById(R.id.unitSpinner);

        //Retrieving the units list from the arrays file from the values package and putting it into the select dosage spinner
        String[] unitsList = getResources().getStringArray(R.array.unit_array);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitsList);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);

        //Pops up a dialog view box where the user can enter the dosage and select a unit
        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String dosage = dosageInputEditText.getText().toString();
            String unit = unitSpinner.getSelectedItem().toString();
            dosageEditText.setText(dosage + " " + unit);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    //This function is responsible for adding new medicine to firebase and medicine schedule
    private void addMedicineToFirestore() {
        //Required variables from layout
        medicineNameEditText = findViewById(R.id.medicineNameEditText);
        dosageEditText = findViewById(R.id.dosageEditText);
        timeEditText = findViewById(R.id.timeEditText);
        daySpinner = findViewById(R.id.daySpinner);
        nameSpinner = findViewById(R.id.nameSpinner);
        requirementsEditText = findViewById(R.id.requirementsEditText);

        String medicineName = medicineNameEditText.getText().toString().trim();
        String dosage = dosageEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String day = daySpinner.getSelectedItem().toString().trim();
        String requirements = requirementsEditText.getText().toString().trim();
        String recipientName = nameSpinner.getCount() > 0 ? nameSpinner.getSelectedItem().toString().trim() : "";
        String recipientId = recipientMap.get(recipientName);


        if (validateInputs(medicineName, dosage, time, day, recipientName, recipientId)) {
        /*  Checks if current user has an Id - here for future functionality (e.g. the user that has added the medicine to schedule might be the primary caregiver that
            gives the medicine to the care recipient which could be displayed in future iterations  */
            String userId = FirebaseUtil.currentUserId();
            if (userId == null || userId.isEmpty()) {
                Log.e("AddMedicineActivity", "User ID is null or empty");
                return;
            }

            //This section of code puts all necessary information into the hashmap which is then added to firebase
            String medicineId = UUID.randomUUID().toString();
            Map<String, Object> medicine = new HashMap<>();
            medicine.put("medicineId", medicineId);
            medicine.put("recipientId", recipientId);
            medicine.put("recipient", recipientName);
            medicine.put("medicineName", medicineName);
            medicine.put("dosage", dosage);
            medicine.put("time", time);
            medicine.put("day", day);
            medicine.put("userId", userId);
            medicine.put("completed", false);

            //If requirements are empty then 'No requirements' is put into firebase
            if (!requirements.isEmpty()) {
                medicine.put("requirements", requirements);
            } else {
                medicine.put("requirements", "No requirements");
            }

            //Adding medicine to firebase and medicine schedule
            FirebaseUtil.getMedicineCollectionReference()
                    .add(medicine)
                    .addOnSuccessListener(documentReference -> {
                        AndroidUtil.showToast(AddMedicineActivity.this, "Medicine added");
                        Intent intent = new Intent(AddMedicineActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> AndroidUtil.showToast(AddMedicineActivity.this, "Error adding medicine" + e.getMessage()));
        }
    }

    //This function is responsible for validation to ensure necessary form information is not empty
    private boolean validateInputs(String medicineName, String dosage, String time, String day, String recipientName, String recipientId){
        boolean hasError = false;
        if (medicineName.isEmpty()) {
            medicineNameEditText.setError("Medicine name cannot be empty");
            hasError = true;
        }
        if (dosage.isEmpty()) {
            dosageEditText.setError("Dosage cannot be empty");
            hasError = true;
        }
        if (time.isEmpty()) {
            timeEditText.setError("Time cannot be empty");
            hasError = true;
        }
        if (day.isEmpty()) {
            AndroidUtil.showToast(this, "Please select a day");
            hasError = true;
        }
        if (recipientName.isEmpty() || recipientId == null) {
            AndroidUtil.showToast(this, "You need to specify who you care for first in the dashboard");
            return false;
        }
        if (hasError) {
            return false;
        }
        return true;
    }

    //This function is responsible for fetching care recipient names to put into the spinner
    private void fetchRecipientNames() {
        FirebaseUtil.getRecipientCollectionReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> recipientNames = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String id = document.getId();
                            recipientMap.put(name, id);
                            recipientNames.add(name);
                        }
                        populateNameSpinner(recipientNames);
                    } else {
                        AndroidUtil.showToast(AddMedicineActivity.this, "Error fetching recipients: " + task.getException());
                    }
                });
    }

    //This function is responsible for populating the spinner with care recipient names using the 'recipientNames' ArrayList that was used to fetch recipients and put inton the ArrayList
    private void populateNameSpinner(List<String> recipientNames) {
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recipientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(adapter);
    }
}