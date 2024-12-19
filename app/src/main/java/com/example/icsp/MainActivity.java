package com.example.icsp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.example.icsp.caregiversChatting.ChatFragment;
import com.example.icsp.checklist.ChecklistFragment;
import com.example.icsp.homepage.CareRecipientModel;
import com.example.icsp.homepage.CareRecipientSpinnerAdapter;
import com.example.icsp.homepage.HomeFragment;
import com.example.icsp.medicine.MedicationFragment;
import com.example.icsp.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity Activity Class
 * <p>
 * This class is responsible for the top toolbar, bottom navigation bar, and displaying the spinners for care recipients in the toolbar
 */

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ChatFragment chatFragment;
    protected ProfileActivity profileActivity;
    private MedicationFragment medicationFragment;
    private HomeFragment homeFragment;
    private List<CareRecipientModel> recipients;
    private ChecklistFragment checklistFragment;
    private Spinner recipientSpinner;
    private SharedViewModel sharedViewModel;
    private CareRecipientSpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Making new instances of activities and fragments
        chatFragment = new ChatFragment();
        profileActivity = new ProfileActivity();
        medicationFragment = new MedicationFragment();
        checklistFragment = new ChecklistFragment();
        homeFragment = new HomeFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recipientSpinner = findViewById(R.id.recipient_spinner);
        recipients = new ArrayList<>();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        fetchCareRecipientsForSpinner();

        //Care recipient spinner on top toolbar allowing the user to select which care recipient to view
        recipientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                CareRecipientModel selectedRecipient = (CareRecipientModel) adapterView.getItemAtPosition(position);
                sharedViewModel.selectRecipient(selectedRecipient);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //NOT NEEDED
            }
        });


        //This section of code is responsible for appropriately displaying the fragments and the bottom navigation bar
        ImageButton searchBtn = findViewById(R.id.main_search_btn);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.menu_home){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,homeFragment).commit();
                searchBtn.setVisibility(View.GONE);
                recipientSpinner.setVisibility(View.GONE);
            }
            if(item.getItemId()==R.id.menu_chat){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatFragment).commit();
                searchBtn.setVisibility(View.VISIBLE);
                recipientSpinner.setVisibility(View.GONE);
            }
            if(item.getItemId()==R.id.menu_medicine){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, medicationFragment).commit();
                searchBtn.setVisibility(View.GONE);
                recipientSpinner.setVisibility(View.VISIBLE);
            }
            if(item.getItemId()==R.id.menu_checklist){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,checklistFragment).commit();
                searchBtn.setVisibility(View.GONE);
                recipientSpinner.setVisibility(View.VISIBLE);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        getToken();
    }

    //This function is responsible for fetching and displaying the care recipients inside the spinner in the toolbar
    private void fetchCareRecipientsForSpinner() {
        FirebaseUtil.getRecipientCollectionReference().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CareRecipientModel recipient = document.toObject(CareRecipientModel.class);
                    recipients.add(recipient);
                }
                spinnerAdapter = new CareRecipientSpinnerAdapter(MainActivity.this, recipients);
                recipientSpinner.setAdapter(spinnerAdapter);
            }
        });
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                FirebaseUtil.getCurrentUser().update("fcmToken",token);
            }
        });
    }
}













