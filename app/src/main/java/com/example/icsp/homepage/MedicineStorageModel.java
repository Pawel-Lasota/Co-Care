package com.example.icsp.homepage;

/**
 * MedicineStorageModel Model Class
 * <p>
 * This class is responsible for structuring the medicine storage items and for getters and setters.
 * Each medicine within the storage has a name, quantity of medicine left, notes and an ID attached to it
 */
public class MedicineStorageModel {
    private String name, quantityLeft, notes, medicineStorageId;

    //Required empty constructor for firebase
    public MedicineStorageModel(){

    }
    public MedicineStorageModel(String name, String quantityLeft, String notes, String medicineStorageId) {
        this.name = name;
        this.quantityLeft = quantityLeft;
        this.notes = notes;
        this.medicineStorageId = medicineStorageId;
    }
    public String getName() {
        return name;
    }
    public String getNotes() {
        return notes;
    }
    public String getQuantityLeft() {
        return quantityLeft;
    }
    public String getMedicineStorageId() {
        return medicineStorageId;
    }
    public void setMedicineStorageId(String medicineStorageId) {
        this.medicineStorageId = medicineStorageId;
    }
}
