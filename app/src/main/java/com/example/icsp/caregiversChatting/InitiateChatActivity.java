package com.example.icsp.caregiversChatting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import com.example.icsp.MainActivity;
import com.example.icsp.R;
import com.example.icsp.UserModel;
import com.example.icsp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * InitiateChatActivity Activity Class
 * <p>
 * This Activity class is responsible for initiating a chat whenever the + is pressed within the chatFragment
 * It displays all users saved into the database for now (can be changed in future iterations to make it more advanced)
 */

public class InitiateChatActivity extends AppCompatActivity {
    private ImageButton backButton;
    private RecyclerView listOfUsers;
    private CaregiverRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        backButton = findViewById(R.id.back_btn);
        listOfUsers = findViewById(R.id.search_user_recycler_view);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(InitiateChatActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        setupSearchRecyclerView();
    }
    private void setupSearchRecyclerView() {
        Query query = FirebaseUtil.getUsersCollectionReference();

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();
        adapter = new CaregiverRecyclerAdapter(options, getApplicationContext());
        listOfUsers.setLayoutManager(new LinearLayoutManager(this));
        listOfUsers.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
}