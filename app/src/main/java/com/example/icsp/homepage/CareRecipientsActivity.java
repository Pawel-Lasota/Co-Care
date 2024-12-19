package com.example.icsp.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.MainActivity;
import com.example.icsp.R;
import com.example.icsp.utils.FirebaseUtil;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * CareRecipientsActivity Activity Class
 * <p>
 * This class is responsible for the fetching and displaying all care recipients inside the care recipient activity within the homepage dashboard.
 */

public class CareRecipientsActivity extends AppCompatActivity {
    private ImageButton backBtn, addBtn;
    private List<CareRecipientModel> recipientList = new ArrayList<>();
    private CareRecipientsAdapter adapter;
    private RecyclerView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_recipients);

        //Retrieving required variables from layout
        backBtn = findViewById(R.id.back_btn);
        addBtn = findViewById(R.id.add_button);
        container = findViewById(R.id.recipient_container);

        //Initialising the adapter and setting it to the recyclerview
        adapter = new CareRecipientsAdapter(recipientList, this);
        container.setAdapter(adapter);
        container.setLayoutManager(new LinearLayoutManager(this));

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CareRecipientsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        addBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CareRecipientsActivity.this, AddRecipientActivity.class);
            startActivity(intent);
        });

        fetchAndDisplayRecipients();
    }
    //Function for fetching from firebase
    private void fetchAndDisplayRecipients() {
        FirebaseUtil.getRecipientCollectionReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recipientList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CareRecipientModel recipient = document.toObject(CareRecipientModel.class);
                            recipient.setRecipientId(document.getId());
                            recipientList.add(recipient);
                        }
                        //Refreshing the list when fetched from firebase
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("CareRecipientsActivity", "Error getting recipients", task.getException());
                    }
                });
    }
}
