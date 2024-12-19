package com.example.icsp.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * FirebaseUtil Utility class
 * <p>
 * This class is responsible for improving the quality of code by reducing the size of main classes and make it more readable.
 */

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        if(currentUserId()!= null){
            return true;
        }
        return false;
    }
    public static DocumentReference getCurrentUser(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static CollectionReference getUsersCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    public static CollectionReference getTaskCollectionReference(){
        return FirebaseFirestore.getInstance().collection("task");
    }
    public static CollectionReference getRecipientCollectionReference(){
        return FirebaseFirestore.getInstance().collection("recipients");
    }
    public static CollectionReference getMedicineCollectionReference(){
        return FirebaseFirestore.getInstance().collection("medicines");
    }
    public static CollectionReference getMedicineStorageCollectionReference(){
        return FirebaseFirestore.getInstance().collection("medicineStorage");
    }
    public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }
    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }
    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return getUsersCollectionReference().document(userIds.get(1));
        }else{
            return getUsersCollectionReference().document(userIds.get(0));
        }
    }
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
}
