package com.example.icsp.checklist;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.icsp.R;
import com.example.icsp.SharedViewModel;
import com.example.icsp.homepage.CareRecipientModel;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * ChecklistFragment Fragment Class
 * <p>
 * This class if responsible for the fragment. It displays the entire checklist in a recyclerview.
 * <p>
 * It also utilises a SharedViewModel which allows for the selected recipient in the MainActivity toolbar to show
 * the checklist for the appropriate selected recipient, even if the user navigates between fragments so it does not reset
 * when the user navigates to a different fragment
 * <p>
 * Also has a floating action button which allows the user to add new tasks upon clicking on the fab.
 */

public class ChecklistFragment extends Fragment implements OnDialogCloseListener{
    private RecyclerView checklistRecyclerView;
    private FloatingActionButton fab;
    private ChecklistAdapter adapter;
    private List<ChecklistModel> checklist;
    public SharedViewModel sharedViewModel;
    private Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checklist, container, false);

        TextView textHolder = getActivity().findViewById(R.id.text_holder);
        textHolder.setText("To-do Checklist");
        
        //Shared view model -
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedRecipient().observe(getViewLifecycleOwner(), this::refreshChecklistForSelectedRecipient);

        checklistRecyclerView = view.findViewById(R.id.checklist_recyclerview);
        fab = view.findViewById(R.id.addFB);

        checklistRecyclerView.setHasFixedSize(true);
        checklistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab.setOnClickListener(v ->{
            ChecklistAdd checklistAdd = ChecklistAdd.newInstance();
            checklistAdd.setOnDialogCloseListener(this);
            checklistAdd.show(getActivity().getSupportFragmentManager(), ChecklistAdd.TAG);
        });

        checklist = new ArrayList<>();
        adapter = new ChecklistAdapter(this, checklist);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(checklistRecyclerView);
        showData();
        checklistRecyclerView.setAdapter(adapter);

        return view;
    }

    //When selecting a recipient, the checklist is refreshed so that the checklist for the other care recipient appears
    private void refreshChecklistForSelectedRecipient(CareRecipientModel careRecipientModel) {
        if (careRecipientModel == null) {
            clearChecklistDisplay();
            return;
        }
        String recipientName = careRecipientModel.getName();
        fetchAndDisplayChecklistItems(recipientName);
    }

    //Fetching the checklist for the appropriate recipient
    private void fetchAndDisplayChecklistItems(String recipientName) {
        CollectionReference tasksRef = FirebaseUtil.getTaskCollectionReference();

        Query recipientQuery = tasksRef.whereEqualTo("recipient", recipientName);
        recipientQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<ChecklistModel> checklist = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ChecklistModel task = document.toObject(ChecklistModel.class);
                checklist.add(task);
            }
            adapter.updateData(checklist);

        }).addOnFailureListener(e -> {
            AndroidUtil.showToast(getContext(), "Error: " + e.getMessage());
            clearChecklistDisplay();
        });
    }
    private void clearChecklistDisplay() {
        checklistRecyclerView.setAdapter(null);
    }
    //Displaying the checklist, ordered by time in descending order
    private void showData() {
        query = FirebaseUtil.getTaskCollectionReference().orderBy("time", Query.Direction.DESCENDING);
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore Error", error.getMessage());
                return;
            }
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        ChecklistModel checklistModel = documentChange.getDocument().toObject(ChecklistModel.class);
                        checklistModel.setTaskId(documentChange.getDocument().getId());
                        checklist.add(checklistModel);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    //Notifying UI change when closing the bottom fragment
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        checklist.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}