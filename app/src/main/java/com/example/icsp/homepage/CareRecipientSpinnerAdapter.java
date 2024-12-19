package com.example.icsp.homepage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.example.icsp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;

/**
 * CareRecipientSpinnerAdapter Adapter Class
 * <p>
 * This class is responsible for creating a spinner that is displayed within the medicine schedule and checklist fragments where the user can switch
 * between care recipients and view their medicine schedule and checklist dedicated to a specified care recipient within the spinner.
 */

public class CareRecipientSpinnerAdapter extends ArrayAdapter<CareRecipientModel> {

    public CareRecipientSpinnerAdapter(Context context, List<CareRecipientModel> recipients) {
        super(context, R.layout.custom_spinner_dropdown_item, recipients);
        setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    //Setting a custom layout for the spinner dropdown that is displayed when clicking on the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_spinner_dropdown_item, parent, false);
        }

        return createItemView(position, convertView, parent);
    }

    //Appropriately displaying the spinner with a main spinner layout, their name, etc.
    private View createItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.main_spinner, parent, false);
        }

        ImageView imgRecipient = convertView.findViewById(R.id.profile_image_view);
        TextView RecipientName = convertView.findViewById(R.id.text_spinner);

        //Get the CareRecipientModel for the current position
        CareRecipientModel recipient = getItem(position);
        if (recipient != null) {
            RecipientName.setText(recipient.getName());
            loadProfileImage(recipient.getRecipientId(), imgRecipient);
        }

        return convertView;
    }

    //Loading profile image within the spinner
    private void loadProfileImage(String recipientId, ImageView imageView) {
        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("recipient_pics/" + recipientId);

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(getContext()).load(uri).placeholder(R.drawable.person_icon).into(imageView);
        }).addOnFailureListener(e -> {
            Log.e("CareRecipientSpinnerAdapter", "Error loading profile image: " + recipientId, e);
        });
    }
}