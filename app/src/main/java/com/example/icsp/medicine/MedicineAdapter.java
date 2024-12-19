package com.example.icsp.medicine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.R;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.List;

/**
 * MedicineAdapter Adapter Class
 * <p>
 * This class is responsible for displaying each medicine within the medicine schedule appropriately
 */

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>{
    private List<MedicineModel> medicineModelList;

    public MedicineAdapter(List<MedicineModel> medicineModelList, Context context) {
        this.medicineModelList = medicineModelList;
        FirebaseApp.initializeApp(context);
    }
    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.displaying_medical_details, parent, false);
        return new MedicineViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        //This section of code is responsible for appropriately displaying information of the medicine in the schedule
        MedicineModel medicineModel = medicineModelList.get(position);
        holder.completedTextView.setVisibility(medicineModel.isCompleted() ? View.VISIBLE : View.GONE);
        holder.medicineNameTextView.setText(medicineModel.getMedicineName());
        holder.dosageTextView.setText("Dosage: " + medicineModel.getDosage());
        holder.timeTextView.setText("Time: " + medicineModel.getTime());
        holder.requirementsTextView.setText("Requirements: " + medicineModel.getRequirements());
        holder.recipientTextView.setText(medicineModel.getRecipient());

        //Clicking on the red X button calls the removeMedicine function for the item at a selected position in the list
        holder.removeMedicineButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                String medicineId = medicineModelList.get(currentPosition).getMedicineId();
                removeMedicine(medicineId, currentPosition);
            }
        });

        //Clicking on the green tick button calls the updateMedicineCompletion function for the item at a selected position in the list
        holder.medicineCompleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                String medicineId = medicineModelList.get(currentPosition).getMedicineId();
                updateMedicineCompletion(medicineId, currentPosition);
            }
        });
    }

    //This function is repsponsibile for updating the medicine at a specific position within the list as completed or not
    private void updateMedicineCompletion(String medicineId, int position) {
        FirebaseUtil.getMedicineCollectionReference()
                .whereEqualTo("medicineId", medicineId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Setting the boolean as either completed or not completed and refreshing the UI change
                            boolean currentCompleted = document.getBoolean("completed");
                            DocumentReference documentReference = FirebaseUtil.getMedicineCollectionReference().document(document.getId());
                            documentReference.update("completed", !currentCompleted)
                                    .addOnSuccessListener(aVoid -> {
                                        medicineModelList.get(position).setCompleted(!currentCompleted);
                                        notifyItemChanged(position);
                                    })
                                    .addOnFailureListener(e -> Log.w("MedicineAdapter", e));
                        }
                    } else {
                        Log.e("MedicineAdapter", "Error getting documents.", task.getException());
                    }
                });
    }

    //Function responsible for deleting the medicine with a specific medicineId and a position in the list then deleting it from firebase by calling the deleteDocument function
    public void removeMedicine(String medicineId, int position) {
        FirebaseUtil.getMedicineCollectionReference()
                .whereEqualTo("medicineId", medicineId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            deleteDocument(documentId, position);
                        }
                    } else {
                        Log.e("MedicineAdapter", "Error getting documents.", task.getException());
                    }
                });
    }

    //Function responsible for deleting the document of the medicine that is being removed
    private void deleteDocument(String docId, int position) {
        DocumentReference medicineRef = FirebaseUtil.getMedicineCollectionReference().document(docId);
        medicineRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("MedicineAdapter", "Medicine deleted with docId: " + docId);
                    medicineModelList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> Log.e("MedicineAdapter", "Error deleting document", e));
    }
    @Override
    public int getItemCount() {
        return medicineModelList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        public TextView medicineNameTextView, dosageTextView, timeTextView, completedTextView, requirementsTextView, recipientTextView;
        public ImageButton removeMedicineButton, medicineCompleteButton;
        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            //Retrieving required variables from layout
            medicineNameTextView = itemView.findViewById(R.id.medicineNameTextView);
            dosageTextView = itemView.findViewById(R.id.dosageTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            removeMedicineButton = itemView.findViewById(R.id.medicine_remove_btn);
            requirementsTextView = itemView.findViewById(R.id.requirementsTextView);
            medicineCompleteButton = itemView.findViewById(R.id.medicine_complete_btn);
            completedTextView = itemView.findViewById(R.id.completedTextView);
            recipientTextView = itemView.findViewById(R.id.recipientTextView);
        }


    }
}
