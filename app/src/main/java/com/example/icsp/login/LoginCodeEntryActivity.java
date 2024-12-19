package com.example.icsp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.icsp.R;
import com.example.icsp.register.UserDetailsActivity;
import com.example.icsp.utils.AndroidUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * LoginCodeEntryActivity Activity Class
 * <p>
 * This class is responsible for sending the required code sent to the users' phone number which the user had to enter to log in.
 * This is known as OTP or One-Time-Password.
 * <p>
 Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 */

public class LoginCodeEntryActivity extends AppCompatActivity {

    private String phoneNumber;
    private Long timeoutSeconds = 30L;
    private String verificationCode;
    protected PhoneAuthProvider.ForceResendingToken resendToken;
    private EditText codeInput;
    private Button nextBtn;
    private ProgressBar progressBar;
    private TextView resendCodeText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        //Retrieving required variables from layout
        codeInput = findViewById(R.id.login_otp);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendCodeText = findViewById(R.id.resend_otp_textview);

        phoneNumber = getIntent().getExtras().getString("phone");

        //Calling the sendCode function to a specific phone number
        sendCode(phoneNumber, false);

        //Clicking the nextBtn checks whether the code entered is correct
        nextBtn.setOnClickListener(v -> {
            String enteredCode = codeInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredCode);
            signIn(credential);
            setInProgress(true);
        });

        //Clicking upon the resend text sends a new code to the phone number
        resendCodeText.setOnClickListener((v) -> {
            sendCode(phoneNumber, true);
        });
    }

    //This function is responsible for sending the code to a specific phone number entered by the user
    private void sendCode(String phoneNumber, boolean isResend){
        //Upon sending of code there is a timer for the resend button to work
        startResendTimer();
        setInProgress(true);
        //This section of code is responsible for sending the code appropriately
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(), "Verification failed: " + e.getMessage());
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                forceResendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "Code sent");
                                setInProgress(false);
                            }
                        });
        //If resend is clicked then the process starts all over again
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendToken).build());
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    //This function is responsible for displaying the spinning wheel for the user to acknowledge that the application is loading and processing in the background
    private void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    //This function is responsible for successfully signing into the application and takes the user to the application
    private void signIn(PhoneAuthCredential phoneAuthCredential){
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Intent intent = new Intent(LoginCodeEntryActivity.this, UserDetailsActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
            }
            else{
                AndroidUtil.showToast(getApplicationContext(),"OTP Verification Failed");
            }
        });
    }

    //This function is responsible for displaying the resend timer appropriately with a 30 second countdown
    private void startResendTimer(){
        resendCodeText.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendCodeText.setText("Resend Code in "+timeoutSeconds+" seconds");
                if(timeoutSeconds<=0){
                    timeoutSeconds = 30L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendCodeText.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }


}