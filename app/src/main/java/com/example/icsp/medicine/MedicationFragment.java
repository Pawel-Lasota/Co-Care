package com.example.icsp.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.R;
import com.example.icsp.SharedViewModel;
import com.example.icsp.homepage.CareRecipientModel;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * MedicationFragment Fragment Class
 * <p>
 * This class is responsible for displaying the medicine schedule within the medicine fragment
 */

public class MedicationFragment extends Fragment {
    private FloatingActionButton addMedicineButton;
    private SharedViewModel sharedViewModel;
    private MedicineAdapter adapter;
    private TextView mondayTextView, tuesdayTextView, wednesdayTextView, thursdayTextView, fridayTextView, saturdayTextView, sundayTextView;
    private RecyclerView mondayRecyclerView, tuesdayRecyclerView, wednesdayRecyclerView, thursdayRecyclerView, fridayRecyclerView, saturdayRecyclerView, sundayRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medication, container, false);

        //Shared view model responsible for displaying the medicine schedule based on the care recipient selected
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedRecipient().observe(getViewLifecycleOwner(), this::refreshMedicationsForSelectedRecipient);

        //Required variables from layout
        mondayTextView = view.findViewById(R.id.mondayTextView);
        tuesdayTextView = view.findViewById(R.id.tuesdayTextView);
        wednesdayTextView = view.findViewById(R.id.wednesdayTextView);
        thursdayTextView = view.findViewById(R.id.thursdayTextView);
        fridayTextView = view.findViewById(R.id.fridayTextView);
        saturdayTextView = view.findViewById(R.id.saturdayTextView);
        sundayTextView = view.findViewById(R.id.sundayTextView);

        mondayRecyclerView = view.findViewById(R.id.mondayMedicineRecyclerView);
        tuesdayRecyclerView = view.findViewById(R.id.TuesdayMedicineRecyclerView);
        wednesdayRecyclerView = view.findViewById(R.id.WednesdayMedicineRecyclerView);
        thursdayRecyclerView = view.findViewById(R.id.ThursdayMedicineRecyclerView);
        fridayRecyclerView = view.findViewById(R.id.FridayMedicineRecyclerView);
        saturdayRecyclerView = view.findViewById(R.id.SaturdayMedicineRecyclerView);
        sundayRecyclerView = view.findViewById(R.id.SundayMedicineRecyclerView);

        //Fragment text holder as 'Medicine Schedule' as we are in the medicine schedule now
        TextView textHolder = getActivity().findViewById(R.id.text_holder);
        textHolder.setText("Medicine Schedule");

        addMedicineButton = view.findViewById(R.id.addFB);
        addMedicineButton.setOnClickListener(v -> openAddMedicineActivity());

        return view;
    }

    //Fetching and displaying medications for the correct day of the week - resembling a physical 'pillbox'
    private void refreshMedicationsForSelectedRecipient(CareRecipientModel careRecipientModel) {
        if (careRecipientModel == null) {
            clearMedicationDisplays();
            return;
        }
        String recipientName = careRecipientModel.getName();
        fetchAndDisplayMedications("Monday", mondayRecyclerView, mondayTextView, recipientName);
        fetchAndDisplayMedications("Tuesday", tuesdayRecyclerView, tuesdayTextView, recipientName);
        fetchAndDisplayMedications("Wednesday", wednesdayRecyclerView, wednesdayTextView, recipientName);
        fetchAndDisplayMedications("Thursday", thursdayRecyclerView, thursdayTextView, recipientName);
        fetchAndDisplayMedications("Friday", fridayRecyclerView, fridayTextView, recipientName);
        fetchAndDisplayMedications("Saturday", saturdayRecyclerView, saturdayTextView, recipientName);
        fetchAndDisplayMedications("Sunday", sundayRecyclerView, sundayTextView, recipientName);
    }

    //clearMedicationDisplays & clearMedicationView functions responsible for hiding the day of the week if there is no medicine for that day of the week
    private void clearMedicationDisplays() {
        clearMedicationView(mondayRecyclerView, mondayTextView);
        clearMedicationView(tuesdayRecyclerView, tuesdayTextView);
        clearMedicationView(wednesdayRecyclerView, wednesdayTextView);
        clearMedicationView(thursdayRecyclerView, thursdayTextView);
        clearMedicationView(fridayRecyclerView, fridayTextView);
        clearMedicationView(saturdayRecyclerView, saturdayTextView);
        clearMedicationView(sundayRecyclerView, sundayTextView);
    }
    private void clearMedicationView(RecyclerView recyclerView, TextView textView) {
        recyclerView.setAdapter(null);
        textView.setVisibility(View.GONE);
    }

    //This function is responsible for fetching and displaying the medications in the medicine schedule
    private void fetchAndDisplayMedications(String day, RecyclerView recyclerView, TextView dayTextView, String recipientName) {
        CollectionReference medicinesRef = FirebaseUtil.getMedicineCollectionReference();

        //Showing the medication shcedule for the correct recipient
        Query dayQuery = medicinesRef.whereEqualTo("day", day);
        dayQuery = dayQuery.whereEqualTo("recipient", recipientName);
        dayQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {

            List<MedicineModel> medicineModelList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                MedicineModel medicineModel = document.toObject(MedicineModel.class);
                medicineModelList.add(medicineModel);
            }
            if (medicineModelList.isEmpty()) {
                dayTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            } else {
                dayTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new MedicineAdapter(medicineModelList, getContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> AndroidUtil.showToast(getContext(), "Fetching and Displaying Medication Failure: " + e.getMessage()));
    }

    private void openAddMedicineActivity() {
        Intent intent = new Intent(requireContext(), AddMedicineActivity.class);
        startActivity(intent);
    }
}

