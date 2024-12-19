package com.example.icsp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.icsp.login.LoginPhoneNumberActivity;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;

/**
 * LaunchSplashActivity Activity Class
 * <p>
 * This class is responsible for displaying a launch activity whilst the main application is loading
 * <p>
 * Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 */

public class LaunchSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //This section of code is responsible for whether the user is already logged in and the app is running in the background
        //then jump straight to the main app without the splash activity
        if(FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.getUsersCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           UserModel model = task.getResult().toObject(UserModel.class);
                           Intent mainIntent = new Intent(this, MainActivity.class);
                           mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                           startActivity(mainIntent);

                           Intent intent = new Intent(this, MainActivity.class);
                           AndroidUtil.passUserModelAsIntent(intent, model);
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(intent);
                           finish();
                       }

                    });
        }else{
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(LaunchSplashActivity.this, MainActivity.class));
                    }else{
                        startActivity(new Intent(LaunchSplashActivity.this, LoginPhoneNumberActivity.class));
                    }
                    finish();
                }
            }, 1000);
        }
    }
}