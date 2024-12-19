package com.example.icsp.caregiversChatting;
import com.google.firebase.Timestamp;

/**
 * ChatMessageModel Model Class
 * <p>
 * Represents a single chat message. It captures the text of the message, identifier of the sender,
 * and the timestamp for when the message has been sent.
 * Also has the necessary getters and setters.
 */

public class ChatMessageModel {
    private String message, senderId;
    private Timestamp timestamp;

    //Required empty constructor for firebase -- DO NOT remove
    public ChatMessageModel() {
    }
    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    public String getMessage() {
        return message;
    }
    public String getSenderId() {
        return senderId;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
