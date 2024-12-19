package com.example.icsp.caregiversChatting;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.icsp.R;
import com.example.icsp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * ChatFragment Fragment Class
 * <p>
 * This fragment displays a list of recent chats with caregivers. It includes functionalities
 * such as displaying the last message, the timestamp of the last message, and an option to remove a chat.
 * Users can continue chatting with caregivers by clicking on a chat item, which opens the conversation
 * where they can send more messages.
 * <p>
 * The fragment uses a recyclerView to list the chats, with each item showing the caregiver's name,
 * the last message, and the time it was sent. Users can initiate new chats by pressing on the recent chatter, which
 * leads them to the InitiateChatActivity.
 * <p>
 * Attention! I do not claim this code as my own and has been coded by @EasyTuto1 on YouTube which will be referenced in the Report under References for Product Development.
 */

public class ChatFragment extends Fragment {
    private RecyclerView recentChatsRecyclerView;
    private RecentCaregiverChatsRecyclerAdapter adapter;
    private ImageButton initiateChatsBtn;

    //Required empty constructor for firebase -- DO NOT remove
    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recentChatsRecyclerView = view.findViewById(R.id.recycler_view);
        TextView textHolder = getActivity().findViewById(R.id.text_holder);
        textHolder.setText("Caregivers Chats");

        initiateChatsBtn = getActivity().findViewById(R.id.main_search_btn);
        //upon clicking on a recent chatter the chat is continued
        initiateChatsBtn.setOnClickListener((v)->{
            startActivity(new Intent(getActivity(), InitiateChatActivity.class));
        });

        setupRecyclerView();
        return view;
    }

    //This function is responsible for putting recent caregivers chatters into the recyclerview list appropriately for the current user that is logged in
    private void setupRecyclerView() {
        Query query = FirebaseUtil.allChatRoomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimeStamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class).build();

        adapter = new RecentCaregiverChatsRecyclerAdapter(options,getContext());
        recentChatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentChatsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    //Listens for changes e.g. new messages being sent or chat updates
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    //Stops listening to preserve resources when fragment is not used
    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();

    }

    //Refreshes the UI if any changes happen
    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();

    }
}
