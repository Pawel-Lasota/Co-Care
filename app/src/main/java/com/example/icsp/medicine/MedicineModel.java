package com.example.icsp.medicine;

/**
 * MedicineModel Model Class
 * <p>
 * This class is responsible for structuring the medicine within the medicine schedule.
 * Each medicine has a name, dosage, time of when to give the medicine, its ID, requirements (e.g. given after meal), recipient it is dedicated to and whether it is
 * completed or not.
 * Includes required getters and setters for its manipulation.
 */
public class MedicineModel {
    private String medicineName;
    private String dosage;
    private String time;
    private String medicineId;
    private String requirements;
    private String recipient;
    private boolean completed;

    //Required empty constructor for Firebase
    public MedicineModel() {
    }
    public MedicineModel(String medicineName, String dosage, String time, String medicineId, String requirements, Boolean completed, String recipient) {
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.time = time;
        this.medicineId = medicineId;
        this.requirements = requirements;
        this.completed = completed;
        this.recipient = recipient;
    }
    public String getRequirements() {
        return requirements;
    }
    public String getMedicineName() {
        return medicineName;
    }
    public String getDosage() {
        return dosage;
    }

    public String getTime() {
        return time;
    }
    public String getMedicineId() {
        return medicineId;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getRecipient() {
        return recipient;
    }

}