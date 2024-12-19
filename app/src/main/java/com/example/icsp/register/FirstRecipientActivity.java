package com.example.icsp.register;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.icsp.MainActivity;
import com.example.icsp.R;
import com.example.icsp.homepage.CareRecipientModel;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * FirstRecipientActivity Activity Class
 * <p>
 * This class is responsible for optionally adding a first care recipient in the sign up process when the user made a new account by logging in for the first time.
 * <p>
 * Special Thanks to dhaval2404 for the image picker library.
 */

public class FirstRecipientActivity extends AppCompatActivity {
    private EditText nameEditText, descriptionEditText, ageEditText;
    private Spinner genderSpinner;
    private TextView skipTextView;
    private Button finishBtn;
    private ImageView recipientPic;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_recipient);

        //Required retrieved layout variables
        nameEditText = findViewById(R.id.recipient_name);
        descriptionEditText = findViewById(R.id.recipient_description);
        ageEditText = findViewById(R.id.recipient_age);
        genderSpinner = findViewById(R.id.recipient_gender_spinner);
        finishBtn = findViewById(R.id.finish_btn_recipient);
        recipientPic = findViewById(R.id.profile_pic_image_view);
        skipTextView = findViewById(R.id.skip_text);

        //The user can skip this activity if they press the Skip text which directs them to the main application
        skipTextView.setOnClickListener(view -> {
            Intent intent = new Intent(FirstRecipientActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        //Image picker so the user can upload a picture of the care recipient (optional)
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
        recipientPic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickLauncher.launch(intent);
        });

        setupGenderSpinner();
        finishBtn.setOnClickListener(view -> saveRecipientInfo());
    }

    //If the user decides to add an image of the care recipient, this function is responsible for adding it into firebase storage with the recipient pic folder named as the recipients ID
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

    //This function is responsible for setting up the gender spinner select box with the arrays from the gender_array in the values package.
    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
    }

    //This function is responsible for saving the care recipient to firestore approprioately
    private void saveRecipientInfo() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        if (validateInputs(name, description, age)) {
            String recipientId = UUID.randomUUID().toString();
            String userId = FirebaseUtil.currentUserId();

            CareRecipientModel recipient = new CareRecipientModel(name, description, age, gender, recipientId);

            Map<String, Object> recipientData = new HashMap<>();
            recipientData.put("name", recipient.getName());
            recipientData.put("description", recipient.getDescription());
            recipientData.put("age", recipient.getAge());
            recipientData.put("gender", recipient.getGender());
            recipientData.put("recipientId", recipient.getRecipientId());
            recipientData.put("userId", userId);

            FirebaseUtil.getRecipientCollectionReference().document(recipientId)
                    .set(recipientData)
                    .addOnSuccessListener(aVoid -> {
                        AndroidUtil.showToast(this, "Recipient added successfully");
                        uploadImageToFirebaseStorage(recipientId);
                        //On success direct the user to the main application
                        Intent intent = new Intent(FirstRecipientActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> AndroidUtil.showToast(this, "Recipient was unable to be added"));
        }

    }

    //This function is responsible for ensuring all required information is entered when completing the add care recipient form
    private boolean validateInputs(String name, String description, String age) {
        boolean hasError = false;
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            hasError = true;
        }
        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            hasError = true;
        }
        if (age.isEmpty()) {
            ageEditText.setError("Age is required");
            hasError = true;
        }
        if(hasError){
            return false;
        }
        return true;
    }


}