package com.example.icsp.caregiversChatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.icsp.MainActivity;
import com.example.icsp.R;
import com.example.icsp.UserModel;
import com.example.icsp.utils.AndroidUtil;
import com.example.icsp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ChatRoomAdapter Adapter Class
 * <p>
 * ChatRoomAdapter is responsible for the chatting activity between two users, creating new chatrooms,
 * sending new messages, entering existing chatrooms and continuing with conversation, etc.
 * <p>
 * Further responsibility is for sending notifications between users when a new message is sent.
 * <p>
 * Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 * Special Thanks to okhttp3 (HTTP Client) for the notification API library.
 */

public class ChatRoomAdapter extends AppCompatActivity {
    private UserModel otherUser;
    private String chatroomId;
    private ChatRoomModel chatroomModel;
    private EditText messageText;
    private ImageButton sendMessage;
    private ImageButton backBtn;
    private TextView otherUserText;
    private RecyclerView chatRoomRecyclerView;
    private ChatRoomRecyclerAdapter adapter;
    private ImageView profilePicImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Retrieves the other and current logged-in user from the chatroom
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        //Retrieves required IDs for the UI layout
        messageText = findViewById(R.id.chat_message_input);
        sendMessage = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUserText = findViewById(R.id.other_username);
        chatRoomRecyclerView = findViewById(R.id.chat_recycler_view);
        profilePicImage = findViewById(R.id.profile_pic_image_view);

        //Retrieves a profile picture from Firebase for the other user based on their ID
        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(this,uri, profilePicImage);
                    }
                });

        //Back Button to go back into the main activity
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChatRoomAdapter.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        //Retrieves the name for the other user
        otherUserText.setText(otherUser.getName());

        //Button to send the message, if the message is empty then nothing is returned
        sendMessage.setOnClickListener(v -> {
            String message = messageText.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessage(message);
        });

        getOrCreateChatRoomModel();
        ChatRecyclerViewSetup();
    }

    /*
    * This function is used for structuring the chat messages, e.g. ordering it via timestamp in descending order, using LinearLayout with each message
    * being below another, a reverse layout for new messages being below the older, etc.
    * It is also listening for any new messages inserted, updating the chatroom with each new message sent.
    */
    private void ChatRecyclerViewSetup() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();
        adapter = new ChatRoomRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chatRoomRecyclerView.setLayoutManager(manager);
        chatRoomRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRoomRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    /*
    * This function is used for sending the messages. When the send message button is pressed, this function is called.
    * It is responsible for timestamping each message, retrieving the sender and storing the last message that has been sent.
    * It also calls the sendNotification function to notify the user that a new message is sent.
    */
    private void sendMessage(String message) {
        chatroomModel.setLastMessageTimeStamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatRoomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
        //getChatRoomReference from the FirebaseUtil class refers to the chatrooms collection in Firebase.
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        messageText.setText("");
                        sendNotification(message);
                    }
                });
    }

    //Responsible for structuring the notification in JSON format and getting the necessary info into the notification
    private void sendNotification(String message) {
        FirebaseUtil.getCurrentUser().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUserModel = task.getResult().toObject(UserModel.class);
                try{
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notificationObject = new JSONObject();
                    assert currentUserModel != null;
                    notificationObject.put("title", "Co-Care: Message from: " + currentUserModel.getName());
                    notificationObject.put("body", message);
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("userId", currentUserModel.getUserId());
                    jsonObject.put("notification", notificationObject);
                    jsonObject.put("data",dataObject);
                    jsonObject.put("to", otherUser.getFcmToken());

                    NotificationApi(jsonObject);
                }catch (Exception e){

                }
            }
        });
    }

    //NotificationApi from OkHTTPClient library establishes the appropriate request for Notifications to be sent
    private void NotificationApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAdkD28NU:APA91bHkMUIFBWCz7EbgiE5kh_05NUGo5X9HErfa-nz4XoXbrfoYecNbdAH9fpgopZLhBG5_v7sFTNv-bvYpPa1v_3aLqdVdJSI87mv1hHRTM2n5fMDp3WKKK3KkfzV5AAi0vCB_drL3")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //NOT NEEDED
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //NOT NEEDED
            }
        });
    }

    //This function is responsible for getting the correct chatroom and creating a new chatroom if users are chatting with each other for the first time
    private void getOrCreateChatRoomModel() {
        FirebaseUtil.getChatRoomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatRoomModel.class);
                //If chatroom doesn't exist then chat is initiated for the first time and a new chat room model is created current logged in user and other user
                if(chatroomModel==null){
                    chatroomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatRoomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
}