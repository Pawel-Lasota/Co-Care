package com.example.icsp.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.example.icsp.R;
import com.hbb20.CountryCodePicker;

/**
 * LoginPhoneNumberActivity Activity Class
 * <p>
 * This class is responsible for appropriately allowing the user to enter their phone number and pick the copuntry code
 * <p>
 * Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 * Special Thanks to hbb20 on GitHub for the country code picker library
 */

public class LoginPhoneNumberActivity extends AppCompatActivity {
    private CountryCodePicker countryCodePicker;
    private EditText phoneInput;
    private Button sendCodeBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        //Retrieving required variables from layout
        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendCodeBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        //Progress bar set to invisible because no loading or processing happening at the start
        progressBar.setVisibility(View.GONE);

        /*  This part of code is responsible for displaying the country code picker and whether the sendCode Button is pressed, data validation is implemented
            to ensure that the phone number is appropriately entered by the user and is valid */
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendCodeBtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid");
                return;
            }
            //On success, navigate the user to the send code activity where the user is required to enter the One-Time-Password to sign in.
            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginCodeEntryActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}