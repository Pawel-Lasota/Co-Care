package com.example.icsp;

import com.google.firebase.Timestamp;

/**
 * UserModel Model Class
 * <p>
 * This class is responsible for structuring user details.
 * Each user has a phone number, name, email, id and a firebase cloud messaging token which is needed for notifications. A timestamp of when the account is created is
 * also included.
 * <p>
 * Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 */

public class UserModel {
    private String phone;
    private String name;
    private String email;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    //Empty constructor required for firebase
    public UserModel() {
    }

    public UserModel(String phone, String name, Timestamp createdTimestamp, String userId, String email) {
        this.phone = phone;
        this.name = name;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getFcmToken() {
        return fcmToken;
    }
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
