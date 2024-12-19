package com.example.icsp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.icsp.UserModel;
import com.example.icsp.checklist.ChecklistModel;

/**
 * AndroidUtil Utility class
 * <p>
 * This class is responsible for improving the quality of code by reducing the size of main classes and make it more readable.
 */

public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username",model.getName());
        intent.putExtra("email", model.getEmail());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFcmToken());
    }
    public static void passingTaskInfo(Bundle bundle, ChecklistModel checklistModel){
        bundle.putString("task" , checklistModel.getTask());
        bundle.putString("deadline" , checklistModel.getDeadline());
        bundle.putString("taskId" , checklistModel.getTaskId());
        bundle.putString("volunteer", checklistModel.getVolunteer());
        bundle.putString("recipient", checklistModel.getRecipient());
        bundle.putString("time", checklistModel.getTime());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setName(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
