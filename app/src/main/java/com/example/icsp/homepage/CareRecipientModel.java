package com.example.icsp.homepage;

/**
 * CareRecipientModel Model class
 * <p>
 * Responsible for structuring the recipient - each recipient has a name, description, age, gender and an ID attached to them.
 * Also has the necessary getters and setters.
 */

public class CareRecipientModel {
    private String name, description, age, gender, recipientId;

    //Required empty constructor for firebase
    public CareRecipientModel(){

    }
    public CareRecipientModel(String name, String description, String age, String gender, String recipientId) {
        this.name = name;
        this.description = description;
        this.age = age;
        this.gender = gender;
        this.recipientId = recipientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public String getAge() {
        return age;
    }
    public String getGender() {
        return gender;
    }
    public String getRecipientId() {
        return recipientId;
    }
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

}
