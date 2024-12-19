package com.example.icsp.caregiversChatting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.R;
import com.example.icsp.UserModel;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * RecentCaregiverChatsRecyclerAdapter Adapter Class
 * <p>
 * This Adapter class is responsible for displaying all users that the current user has recently chatted with
 * <p>
 * Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 */
public class RecentCaregiverChatsRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentCaregiverChatsRecyclerAdapter.ChatroomModelViewHolder> {
    private Context context;
    public RecentCaregiverChatsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        //Retrieving the profile picture for each user
                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()) {
                                        Uri uri = t.getResult();
                                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                    }
                                });
                        //Current user appears in the list with a (Me) next to the name
                        if (otherUserModel.getUserId().equals(FirebaseUtil.currentUserId())) {
                            holder.nameText.setText(otherUserModel.getName() + " (Me)");
                        } else {
                            holder.nameText.setText(otherUserModel.getName());
                        }
                        //If last message sent by current user then display the recent message with a 'You:' in front of it
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        if (lastMessageSentByMe) {
                            holder.lastMessageText.setText("You : " + model.getLastMessage());
                        } else {
                            holder.lastMessageText.setText(model.getLastMessage());
                        }
                        //Clicking on a user launches the chatting activity for conversations
                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ChatRoomAdapter.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimeStamp()));
                        holder.deleteButton.setOnClickListener(view -> deleteItem(position));
                    }
                });
    }

    //Function to remove a user from the recent chatters list
    private void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete()
                .addOnSuccessListener(aVoid -> {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }
    public static class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        private final TextView lastMessageText, lastMessageTime, nameText;
        private final ImageView profilePic, deleteButton;
        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
