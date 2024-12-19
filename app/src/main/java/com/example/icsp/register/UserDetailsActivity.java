package com.example.icsp.register;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.example.icsp.MainActivity;
import com.example.icsp.R;
import com.example.icsp.UserModel;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.Timestamp;

/**
 * UserDetailsActivity Activity Class
 * <p>
 * This class is responsible for registering the caregiver / user by them having to enter their information
 * NOTE: Email is not used for anything at the moment although future functionalities like having an alternative way to log in if phone number is not accessible is beneficial
 */

public class UserDetailsActivity extends AppCompatActivity {
    private EditText nameInput, emailInput;
    private Button proceedBtn;
    private String name, email;
    private ProgressBar progressBar;
    private String phoneNumber;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        //Retrieving required layout variables
        nameInput = findViewById(R.id.login_name);
        proceedBtn = findViewById(R.id.proceed_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        emailInput = findViewById(R.id.login_email);

        //Passing on the phone number from previous activity as the user is signing up with their specific phone number
        phoneNumber = getIntent().getExtras().getString("phone");
        getUsernameAndEmail();

        proceedBtn.setOnClickListener((v ->{
            setUsernameAndEmail();
        }));
    }

    //This function is triggered when the proceed button is clicked. It performs data validation and creates a new user model which is set as the current user
    private void setUsernameAndEmail(){
        name = nameInput.getText().toString();
        email = emailInput.getText().toString();

        if(validateInputs(name, email)) {
            FirebaseUtil.getCurrentUser().set(userModel).addOnCompleteListener(task -> {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(UserDetailsActivity.this, FirstRecipientActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    //This function is responsible for checking if an account has been created and allows the user to skip the registration process and go straight to the main app
    private void getUsernameAndEmail(){
        setInProgress(true);
        FirebaseUtil.getCurrentUser().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if(userModel != null){
                    if(userModel.getName() != null && !userModel.getName().isEmpty() && userModel.getEmail() != null && !userModel.getEmail().isEmpty()){
                        Intent intent = new Intent(UserDetailsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        nameInput.setText(userModel.getName());
                        emailInput.setText(userModel.getEmail());
                    }
                }
            }
        });
    }

    //This function is responsible for data validation when completing the sign up form
    private boolean validateInputs(String name, String email){
        if(name.isEmpty() || name.length()<3){
            nameInput.setError("Username length should be at least 3 characters");
            return false;
        }
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Invalid email address");
            return false;
        }
        setInProgress(true);
        if(userModel!=null){
            userModel.setName(name);
            userModel.setEmail(email);
        }else{
            userModel = new UserModel(phoneNumber,name,Timestamp.now(),FirebaseUtil.currentUserId(), email);
        }
        return true;
    }

    //This function is responsible for setting up a spinning wheel when the application is processing in the background for the user to acknowledge it is loading
    private void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            proceedBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            proceedBtn.setVisibility(View.VISIBLE);
        }
    }

}