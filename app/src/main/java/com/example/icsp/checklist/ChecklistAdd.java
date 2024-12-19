package com.example.icsp.checklist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.icsp.R;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ChecklistAdd Fragment Class
 * <p>
 * This class is responsible for adding new to-do tasks to the checklist. It also handles editing existing tasks and updating them in firebase based on their task ID.
 */

public class ChecklistAdd extends BottomSheetDialogFragment {
    protected static final String TAG = "AddNewTask";
    private static final String DEFAULT_DUE_DATE_TEXT = "Set Due Date";
    private static final String DEFAULT_DUE_TIME_TEXT = "Set Due Time";
    private HashMap<String, String> recipientsMap = new HashMap<>();
    private EditText taskEdit;
    private Button saveBtn;
    private Context context;
    private String taskId, dueDateUpdate, dueTimeUpdate;
    private boolean isUpdate = false;
    private OnDialogCloseListener onDialogCloseListener;
    private TextView setDueTime, setDueDate;
    private Spinner allocateTask, recipientTask;
    private HashMap<String, String> caregiversMap = new HashMap<>();

    //Required empty constructor -- DO NOT remove
    public ChecklistAdd() {
    }
    public static ChecklistAdd newInstance(){
        return new ChecklistAdd();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);
    }

    //Refers to the onDialogCloseListener interface
    public void setOnDialogCloseListener(OnDialogCloseListener listener) {
        this.onDialogCloseListener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Retrieving necessary variables from the layout
        setDueDate = view.findViewById(R.id.set_due_date);
        taskEdit = view.findViewById(R.id.task_edit_text);
        saveBtn = view.findViewById(R.id.save_btn);
        allocateTask = view.findViewById(R.id.allocate_task);
        setDueTime = view.findViewById(R.id.set_due_time);
        recipientTask = view.findViewById(R.id.recipient_task);

        fetchCaregivers();
        fetchRecipients();

        //This section of the code is used for setting new task information
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            taskId = bundle.getString("taskId");
            dueDateUpdate = bundle.getString("deadline");
            dueTimeUpdate = bundle.getString("time");

            taskEdit.setText(task);
            setDueDate.setText(dueDateUpdate);
            setDueTime.setText(dueTimeUpdate);
        }

        //Clicking on the time icon takes the calendar instance with current hour and minute and brings out a time picker with the current hour and minute.
        setDueTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view1, hourOfDay, minute1) -> {
                String formattedTime = String.format("%02d:%02d", hourOfDay, minute1);
                setDueTime.setText(formattedTime);
            }, hour, minute, true);

            timePickerDialog.show();
        });

        //Similarly for clicking on the date, a calendar instance is created with current date and a date picker is brought out with current date.
        setDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int MONTH = calendar.get(Calendar.MONTH);
            int YEAR = calendar.get(Calendar.YEAR);
            int DAY = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
                month = month + 1;
                String formattedDate = dayOfMonth + "/" + month + "/" + year;
                setDueDate.setText(formattedDate);
            }, YEAR, MONTH, DAY);
            datePickerDialog.show();
        });

        //Clicking on the save button checks if the required form fields are not empty and saves the task if not empty
        saveBtn.setOnClickListener(v -> {
            String task = taskEdit.getText().toString();
            String deadline = setDueDate.getText().toString();
            String time = setDueTime.getText().toString();
            String selectedUserName = allocateTask.getSelectedItem().toString();

            //Validation
            if (task.isEmpty() || deadline.equals(DEFAULT_DUE_DATE_TEXT) || time.equals(DEFAULT_DUE_TIME_TEXT)) {
                AndroidUtil.showToast(context, "Please fill in all fields.");
                return;
            }

            //If successfully updated
            if(isUpdate){
                FirebaseUtil.getTaskCollectionReference().document(taskId).update("task", task, "deadline", deadline, "volunteer", selectedUserName, "time", time)
                        .addOnSuccessListener(aVoid -> {
                            AndroidUtil.showToast(context, "Task updated");
                            dismiss();
                        })
                        .addOnFailureListener(e -> AndroidUtil.showToast(context, "Task Not Updated - " + e.getMessage()));
            }
            else {
                if (!task.isEmpty() && !deadline.isEmpty() && !time.isEmpty() ) {
                    String taskId = UUID.randomUUID().toString();
                    String userId = FirebaseUtil.currentUserId();
                    if (userId == null || userId.isEmpty()) {
                        Log.e("AddNewTask", "User ID is null or empty");
                        return;
                    }

                    //Adding new tasks to the firebase
                    String selectedRecipientName = recipientTask.getSelectedItem().toString();
                    String recipientId = recipientsMap.get(selectedRecipientName);
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("userId", userId);
                    taskMap.put("taskId", taskId);
                    taskMap.put("recipientId", recipientId);
                    taskMap.put("recipient", selectedRecipientName);
                    taskMap.put("task", task);
                    taskMap.put("deadline", deadline);
                    taskMap.put("status", 0);
                    taskMap.put("volunteer", selectedUserName);
                    taskMap.put("timestamp", FieldValue.serverTimestamp());
                    taskMap.put("time", time);

                    FirebaseUtil.getTaskCollectionReference().document(taskId).set(taskMap)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    AndroidUtil.showToast(context, "Task added successfully");
                                    dismiss();
                                } else {
                                    AndroidUtil.showToast(context, "Something went wrong");
                                }
                            }).addOnFailureListener(e -> AndroidUtil.showToast(context, e.getMessage()));
                } else {
                    AndroidUtil.showToast(context, "Cannot leave any blanks");
                }
            }
        });
    }

    //Fetching recipients for the select box
    private void fetchRecipients() {
        FirebaseUtil.getRecipientCollectionReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> recipientNames = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String id = document.getId();
                            recipientsMap.put(name, id);
                            recipientNames.add(name);
                        }
                        populateRecipientSpinner(recipientNames);
                    } else {
                        Log.e(TAG, "Error fetching recipients", task.getException());
                    }
                });
    }

    //Support function for populating the select box
    private void populateRecipientSpinner(List<String> recipientNames) {
        Spinner recipientSpinner = getView().findViewById(R.id.recipient_task);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, recipientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipientSpinner.setAdapter(adapter);
    }

    //Fetching caregivers for the select box
    private void fetchCaregivers(){
        FirebaseUtil.getUsersCollectionReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> userNames = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String id = document.getId();
                            caregiversMap.put(name, id);
                            userNames.add(name);
                        }
                        populateCaregiverSpinner(userNames);
                        Bundle bundle = getArguments();
                        if (bundle != null) {
                            String volunteer = bundle.getString("volunteer");
                            if (volunteer != null && !volunteer.isEmpty()) {
                                int spinnerPosition = ((ArrayAdapter<String>) allocateTask.getAdapter()).getPosition(volunteer);
                                allocateTask.setSelection(spinnerPosition);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching users", task.getException());
                    }
                });
    }

    //Support function for populating the select box
    private void populateCaregiverSpinner(List<String> userNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, userNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allocateTask.setAdapter(adapter);
    }

    //Brings out the bottom fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = context;
        }
    }

    //Clicking away and dismissing the bottom fragment
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
        if (onDialogCloseListener != null) {
            onDialogCloseListener.onDialogClose(dialog);
        }
    }
}
