package com.example.icsp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * ProfileActivity Activity Class
 * <p>
 * This class is responsible for the profile activity where the caregiver can change their profile picture or name.
 * <p>
 * Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 * Special Thanks to dhaval2404 for the image picker library.
 */

public class ProfileActivity extends AppCompatActivity {
    private ImageView profilePic, backBtn;
    private EditText nameInput, phoneInput, emailInput;
    private Button updateProfileBtn;
    private TextView logoutBtn;
    private UserModel currentUserModel;
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        //Retrieving required layout variables
        profilePic = findViewById(R.id.profile_image_view);
        nameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        emailInput = findViewById(R.id.profile_email);
        updateProfileBtn = findViewById(R.id.profile_update_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        backBtn = findViewById(R.id.back_btn);

        //imagePickLauncher used for allowing the user to pick new images from their devices
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(this, selectedImageUri, profilePic);
                        }
                    }
                });

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));

        backBtn.setOnClickListener((v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }));

        //LogoutBtn - signs the user out and takes them to the beginning
        logoutBtn.setOnClickListener((v) -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUtil.logout();
                    Intent intent = new Intent(ProfileActivity.this, LaunchSplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        });

        //Clicking on the profile pic in the profile activity opens the imagePickLauncher - image requirements are also included
        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });
    }

    //This function is responsible for updating the caregivers information with specific requirements, e.g. a new name is to have at least 3 characters
    private void updateBtnClick(){
        String newName = nameInput.getText().toString();
        if(newName.isEmpty() || newName.length()<3){
            nameInput.setError("Username length should be at least 3 chars");
            return;
        }
        currentUserModel.setName(newName);
        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        }else{
            updateToFirestore();
        }

    }

    //This function is responsible for updating the information in firebase
    private void updateToFirestore(){
        FirebaseUtil.getCurrentUser().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(this,"Updated successfully");
                    }else{
                        AndroidUtil.showToast(this,"Updated failed");
                    }
                });
    }

    //This function is responsible for retrieving current information from firebase for the current user
    private void getUserData() {
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(this, uri, profilePic);
                    }
                });

        FirebaseUtil.getCurrentUser().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            nameInput.setText(currentUserModel.getName());
            emailInput.setText(currentUserModel.getEmail());
            phoneInput.setText(currentUserModel.getPhone());
        });
    }
}













