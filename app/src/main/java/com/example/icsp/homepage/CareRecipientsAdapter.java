package com.example.icsp.homepage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.icsp.R;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;

/**
 * CareRecipientsAdapter Adapter class
 * <p>
 * This class is responsible for appropriately displaying each care recipient and also has a function to remove the care recipient from firebase.
 */

public class CareRecipientsAdapter extends RecyclerView.Adapter<CareRecipientsAdapter.CareRecipientsViewHolder> {

    private List<CareRecipientModel> recipientsList;
    private Context context;

    public CareRecipientsAdapter(List<CareRecipientModel> recipientsList, Context context) {
        this.recipientsList = recipientsList;
        this.context = context;
        FirebaseApp.initializeApp(context);
    }

    @NonNull
    @Override
    public CareRecipientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.displaying_recipient_details, parent, false);
        return new CareRecipientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CareRecipientsAdapter.CareRecipientsViewHolder holder, int position) {

        //This section of code is responsible for appropriately displaying each care recipient and their information
        CareRecipientModel careRecipientModel = recipientsList.get(position);
        holder.nameTextView.setText(careRecipientModel.getName());
        holder.ageTextView.setText("Age: " + careRecipientModel.getAge());
        holder.genderTextView.setText("Gender: " + careRecipientModel.getGender());
        holder.descTextView.setText(careRecipientModel.getDescription());
        holder.removeBtn.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();
            /*  If a care recipient at a specific position in the list is removed from the recycler view, then the recipient is also removed from firebase.
                It is made that way so that the user can click on a remove button for a specific care recipient in a list and that care recipient at that position
                will be removed   */
            if (currentPosition != RecyclerView.NO_POSITION) {
                String recipientId = recipientsList.get(currentPosition).getRecipientId();
                removeRecipient(recipientId, currentPosition);
            }
        });
        //Clicking on edit text takes the user to the add care recipient activity class which consists of a form with already fetched existing information for editing
        holder.editTextView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddRecipientActivity.class);
            intent.putExtra("recipientId", careRecipientModel.getRecipientId());
            context.startActivity(intent);
        });

        //If a care recipient exists then their picture is fetched from firebase storage
        if (careRecipientModel.getRecipientId() != null && !careRecipientModel.getRecipientId().isEmpty()) {
            StorageReference profileImageRef = FirebaseStorage.getInstance()
                    .getReference("recipient_pics/" + careRecipientModel.getRecipientId());

            profileImageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(context).load(uri).into(holder.profileImageView);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CareRecipientsAdapter", "Error loading profile image", e);
                    });
        }

    }
    //Function responsible for removing the care recipient at a specified position in the list
    private void removeRecipient(String recipientId, int currentPosition) {
        DocumentReference recipientRef = FirebaseUtil.getRecipientCollectionReference().document(recipientId);
        recipientRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("CareRecipientsAdapter", "Recipient deleted with ID: " + recipientId);
                    recipientsList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                })
                .addOnFailureListener(e -> Log.w("CareRecipientsAdapter", "Error deleting document", e));
    }


    @Override
    public int getItemCount() {
        return recipientsList.size();
    }

    public static class CareRecipientsViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, descTextView, ageTextView, genderTextView, editTextView;
        private ImageButton removeBtn;
        private ImageView profileImageView;
        public CareRecipientsViewHolder(@NonNull View itemView) {
            super(itemView);
            //Retrieving required variables from layout
            nameTextView = itemView.findViewById(R.id.recipientName);
            descTextView = itemView.findViewById(R.id.recipientDescription);
            ageTextView = itemView.findViewById(R.id.recipientAge);
            genderTextView = itemView.findViewById(R.id.recipientGender);
            editTextView = itemView.findViewById(R.id.recipient_edit_btn);
            removeBtn = itemView.findViewById(R.id.recipient_remove_btn);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
        }
    }
}

