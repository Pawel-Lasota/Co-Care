package com.example.icsp.checklist;
import static android.content.ContentValues.TAG;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.R;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import java.util.List;

/**
 * ChecklistAdapter Adapter Class
 * <p>
 * This adapter class is responsible for displaying the checklists in the ChecklistFragment appropriately.
 * It is also responsible for retrieving correct data for editing to-do tasks
 */
public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {
    private List<ChecklistModel> todoList;
    private final ChecklistFragment checklistFragment;
    public ChecklistAdapter(ChecklistFragment checklistFragment, List<ChecklistModel> todoList){
        this.todoList = todoList;
        this.checklistFragment = checklistFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(checklistFragment.getActivity()).inflate(R.layout.checklist_tasks, parent, false);
        return new MyViewHolder(view);
    }

    /*  Function used for deleting the task from the checklist by deleting it from Firebase which the checklists fetches the data from
        it removes the task at the correct position in the list */

    public Context getContext(){
        return checklistFragment.getActivity();
    }

    /*  Function used for editing the task. It retrieves the necessary current information which the user can change and it will
        update with new information */
    public void editTask(int position){
        ChecklistModel checklistModel = todoList.get(position);
        Bundle bundle = new Bundle();
        AndroidUtil.passingTaskInfo(bundle, checklistModel);
        ChecklistAdd checklistAdd = new ChecklistAdd();
        checklistAdd.setArguments(bundle);
        checklistAdd.show(checklistFragment.getActivity().getSupportFragmentManager(), ChecklistAdd.TAG);
    }

    /*  Function for correctly setting the information in the checklist by retrieving all information from the model.
        If the task checkbox is checked then the current task is deleted from firebase based on its taskId.
        The deletion has a 500ms delay so that the checkbox can be seen checked and then it is deleted. */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChecklistModel checklistModel = todoList.get(position);
        holder.checkBox.setText(checklistModel.getTask());
        holder.dueDate.setText(checklistModel.getDeadline() + " at");
        holder.checkBox.setChecked(toBoolean(checklistModel.getStatus()));
        holder.volunteer.setText("Task for: " + checklistModel.getVolunteer());
        holder.dueTime.setText(checklistModel.getTime());
        holder.recipient.setText(checklistModel.getRecipient());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                FirebaseUtil.getTaskCollectionReference().document(checklistModel.getTaskId()).delete()
                        .addOnSuccessListener(aVoid -> {
                            new Handler().postDelayed(() -> {
                                if (position != RecyclerView.NO_POSITION) {
                                    todoList.remove(position);
                                    notifyItemRemoved(position);
                                }
                            }, 500);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error deleting task: " + e.getMessage());
                        });
            }
        });
    }

    /*  Converts an integer value to a boolean. If the status is anything other than 0 then the checkbox is checked (true) and if not 0 then it is unchecked (false).
        Although, this is not really necessary as the task gets deleted from firestore on checking the checkbox but for future functionality perhaps, the status is kept */
    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    //Function for passing new updated information, setting it appropriately in the list and refreshing the UI.
    public void updateData(List<ChecklistModel> newData) {
        this.todoList = newData;
        notifyDataSetChanged();
    }

    //Retrieving the necessary variables from the layout
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView volunteer, recipient, dueDate, dueTime;
        private final CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            volunteer = itemView.findViewById(R.id.checklist_allocate);
            dueDate = itemView.findViewById(R.id.checklist_due_date);
            checkBox = itemView.findViewById(R.id.m_checkbox);
            dueTime = itemView.findViewById(R.id.checklist_due_time);
            recipient = itemView.findViewById(R.id.checklist_recipient);
        }
    }
}
