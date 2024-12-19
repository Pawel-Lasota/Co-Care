package com.example.icsp.caregiversChatting;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.R;
import com.example.icsp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * ChatRoomRecyclerAdapter Adapter Class
 * <p>
 * This adapter class is responsible for the layout of the chatroom. Attaching a date and time to each message.
 * <p>
 * Attention! I do not claim this code as fully my own (Although the format date, shouldShowDate, formatTimeStamp functions are my contributions).
 * and has been mainly coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 */
public class ChatRoomRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRoomRecyclerAdapter.ChatModelViewHolder> {
    private final Context context;
    public ChatRoomRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText(model.getMessage());
            holder.rightChatTimestamp.setText(formatTimestamp(model.getTimestamp()));
        }else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextView.setText(model.getMessage());
            holder.leftChatTimestamp.setText(formatTimestamp(model.getTimestamp()));
        }
        if (shouldShowDate(position)) {
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(formatDate(model.getTimestamp()));
        } else {
            holder.dateTextView.setVisibility(View.GONE);
        }
    }
    private String formatDate(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }
    private boolean shouldShowDate(int position) {
        //Shows date for the messages
        if (position == 0) {
            return true;
        }
        Timestamp currentMessageTimestamp = getItem(position).getTimestamp();
        Timestamp previousMessageTimestamp = getItem(position - 1).getTimestamp();

        LocalDate currentDate = currentMessageTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate previousDate = previousMessageTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return !currentDate.equals(previousDate);
    }
    private String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }
    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }
    public static class ChatModelViewHolder extends RecyclerView.ViewHolder{
        private final RelativeLayout leftChatLayout, rightChatLayout;
        private final TextView leftChatTextView,rightChatTextView;
        private final TextView leftChatTimestamp, rightChatTimestamp;
        private final TextView dateTextView;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
            leftChatTimestamp = itemView.findViewById(R.id.left_chat_timestamp);
            rightChatTimestamp = itemView.findViewById(R.id.right_chat_timestamp);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }
    }
}
