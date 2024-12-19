package com.example.icsp.homepage;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.icsp.R;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AddRecipientActivity Activity Class
 * <p>
 * This class is responsible for adding care recipients allowing users to have more than one care recipients.
 * It is also responsible for editing and updating existing care recipients.
 * <p>
 * If a care recipient has its information changed, then there are functions in this class which are responsible for
 * changing the care recipients details in other aspects of the application.
 */

public class AddRecipientActivity extends AppCompatActivity {
    private EditText recipientNameEditText, requirementsEditText, ageEditText;
    private String recipientName, recipientAge, recipientGender, recipientRequirements;
    private Spinner genderSpinner;
    private Button saveButton;
    private ImageButton backBtn;
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;
    private ImageView recipientPic;
    private String recipientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipient);

        //Required layout variables
        recipientNameEditText = findViewById(R.id.RecipientNameEditText);
        requirementsEditText = findViewById(R.id.requirementsEditText);
        ageEditText = findViewById(R.id.ageEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        saveButton = findViewById(R.id.addFB);
        backBtn = findViewById(R.id.back_btn);
        recipientPic = findViewById(R.id.profile_image_view);

        //Responsible for launching an image picker and selecting images, e.g. in a gallery
        imagePickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        recipientPic.setImageURI(imageUri);
                        selectedImageUri = imageUri;
                    }
                }
        );

        //Clicking on a picture of a care recipient brings out the image launcher
        recipientPic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickLauncher.launch(intent);
        });

        //Loading all recipients based on their recipient Id
        recipientId = getIntent().getStringExtra("recipientId");
        if (recipientId != null && !recipientId.isEmpty()) {
            loadRecipientData(recipientId);
        }

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        //This adapter is required for appropriately displaying information in the spinner for gender
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        //Save button - an existing recipient has its information updated but a new recipient is added to Firebase newly
        saveButton.setOnClickListener(view -> {
            if (recipientId != null) {
                updateRecipient();
            } else {
                addRecipientToFireStore();
            }
        });
    }

    //Function responsible for uploading the recipient picture to firebase storage when a picture is added under the recipient_pics folder
    private void uploadImageToFirebaseStorage(String recipientId) {
        if (selectedImageUri != null) {
            StorageReference profileImageRef = FirebaseStorage.getInstance()
                    .getReference("recipient_pics/" + recipientId);

            profileImageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("AddRecipientActivity", "Profile image uploaded");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AddRecipientActivity", "Failed to upload image", e);
                    });
        }
    }

    //Function responsible for updating an existing recipients information to firebase
    private void updateRecipient() {
        String updatedName = recipientNameEditText.getText().toString();
        String requirements = requirementsEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        loadRecipientProfileImage(recipientId);

        //Retrieving existing information
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", updatedName);
        updatedData.put("description", requirements);
        updatedData.put("age", age);
        updatedData.put("gender", gender);

        //Updating information
        FirebaseUtil.getRecipientCollectionReference().document(recipientId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    uploadImageToFirebaseStorage(recipientId);
                    AndroidUtil.showToast(AddRecipientActivity.this, "Recipient updated successfully");
                    updateMedicinesWithNewRecipientName(recipientId, updatedName);
                    updateTasksWithNewRecipientName(recipientId, updatedName);
                    //On success open the care recipients activity
                    Intent intent = new Intent(AddRecipientActivity.this, CareRecipientsActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    AndroidUtil.showToast(AddRecipientActivity.this, "Error updating recipient: " + e.getMessage());
                });
    }

    //Function responsible for changing information inside the medicine schedule fragment whenever a recipient has their information updated
    private void updateMedicinesWithNewRecipientName(String recipientId, String newName) {
        FirebaseUtil.getMedicineCollectionReference()
                .whereEqualTo("recipientId", recipientId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DocumentReference medicineRef = document.getReference();
                        medicineRef.update("recipient", newName);
                    }
                })
                .addOnFailureListener(e -> Log.e("UpdateMedicine", "Error updating recipient name in medicines", e));
    }

    //Function responsible for changing information inside the checklist fragment whenever a recipient has their information updated
    private void updateTasksWithNewRecipientName(String recipientId, String newName) {
        FirebaseUtil.getTaskCollectionReference()
                .whereEqualTo("recipientId", recipientId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DocumentReference taskRef = document.getReference();
                        taskRef.update("recipient", newName);
                    }
                })
                .addOnFailureListener(e -> Log.e("UpdateTask", "Error updating recipient name in tasks", e));
    }

    //Function responsible for loading recipient profile pictures appropriately
    private void loadRecipientProfileImage(String recipientId) {
        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("recipient_pics/" + recipientId);

        profileImageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).into(recipientPic);
                })
                .addOnFailureListener(e -> {
                    Log.e("AddRecipientActivity", "Error loading profile image", e);
                });
    }

    //Function responsible for loading recipient data appropriately when a recipient exists in the care recipients activity
    private void loadRecipientData(String recipientId) {
        FirebaseUtil.getRecipientCollectionReference().document(recipientId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        loadRecipientProfileImage(recipientId);
                        CareRecipientModel recipient = documentSnapshot.toObject(CareRecipientModel.class);
                        recipientNameEditText.setText(recipient.getName());
                        requirementsEditText.setText(recipient.getDescription());
                        ageEditText.setText(recipient.getAge());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddRecipientActivity", "Error loading data", e);
                });
    }

    //Function responsible for adding new care recipients to firebase
    private void addRecipientToFireStore() {
        recipientName = recipientNameEditText.getText().toString();
        recipientRequirements = requirementsEditText.getText().toString();
        recipientAge = ageEditText.getText().toString();
        recipientGender = genderSpinner.getSelectedItem().toString();

        if(validateInputs(recipientName, recipientRequirements, recipientAge)) {

        /*  Retrieving current user ID as it will be added in the same document as the care recipient to know who added the care recipient.
            This could be useful in future UCD iterations such as displaying the person who entered the care recipient as the primary caregiver
            and therefore any important queries regarding this care recipient could be fulfilled through messaging this caregiver   */
            String userId = FirebaseUtil.currentUserId();
            String recipientId = UUID.randomUUID().toString();
            if (userId == null || userId.isEmpty()) {
                Log.e("AddMedicineActivity", "User ID is null or empty");
                return;
            }
            Map<String, Object> recipient = new HashMap<>();
            recipient.put("userId", userId);
            recipient.put("recipientId", recipientId);
            recipient.put("name", recipientName);
            recipient.put("description", recipientRequirements);
            recipient.put("age", recipientAge);
            recipient.put("gender", recipientGender);
            FirebaseUtil.getRecipientCollectionReference().document(recipientId).set(recipient)
                    .addOnSuccessListener(documentReference -> {
                        uploadImageToFirebaseStorage(recipientId);
                        AndroidUtil.showToast(AddRecipientActivity.this, "Recipient added");
                        //On success - open the care recipients activity
                        Intent intent = new Intent(AddRecipientActivity.this, CareRecipientsActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> AndroidUtil.showToast(AddRecipientActivity.this, "Error adding recipient" + e.getMessage()));
        }
    }

    //Function for validation to ensure all information is appropriately entered
    private boolean validateInputs(String recipientName, String recipientRequirements, String recipientAge){
        boolean hasError = false;
        if (recipientName.isEmpty() || recipientName.length()>15) {
            recipientNameEditText.setError("Name cannot be empty");
            hasError = true;
        }
        if(recipientName.length()>15){
            recipientNameEditText.setError("Name cannot exceed 15 characters");
        }
        if (recipientRequirements.isEmpty()) {
            requirementsEditText.setError("Description cannot be empty");
            hasError = true;
        }
        if (recipientAge.isEmpty()) {
            ageEditText.setError("Age cannot be empty");
            hasError = true;
        }
        if (hasError) {
            return false;
        }else{
            return true;
        }
    }
}