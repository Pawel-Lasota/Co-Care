package com.example.icsp.caregiversChatting;
import com.google.firebase.Timestamp;
import java.util.List;

/**
 * ChatRoomModel Model Class
 * <p>
 * ChatRoomModel provides the structure for each chatroom.
 * Each chatroom has an Id, two userIds involved in the conversation, message timestamp, senderId and the actual last message.
 * Also has the necessary getters and setters.
 * <p>
 */
public class ChatRoomModel {
    protected String chatroomId;
    private List<String> userIds;
    private Timestamp lastMessageTimeStamp;
    private String lastMessageSenderId;
    private String lastMessage;

    //Required empty constructor for Firebase -- DO NOT remove
    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimeStamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }
    public List<String> getUserIds() {
        return userIds;
    }
    public Timestamp getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }
    public void setLastMessageTimeStamp(Timestamp lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }
    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }
    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
    public String getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
