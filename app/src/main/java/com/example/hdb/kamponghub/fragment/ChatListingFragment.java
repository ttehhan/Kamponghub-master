package com.example.hdb.kamponghub.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.adapter.ChatListingAdapter;
import com.example.hdb.kamponghub.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.example.hdb.kamponghub.models.MyApplication;

//Uses in class FirebaseRecycleAdapter

public class ChatListingFragment extends Fragment {

    public ChatListingFragment() {
        // Required empty public constructor
    }

    //This constant is for easy referencing for Log purposes
    private static final String TAG = ChatListingFragment.class.getSimpleName();

    //Layout
    private RecyclerView chatList;
    private LinearLayoutManager layoutManager;
    private ChatListingAdapter chatAdapter;
    private ProgressDialog dialog;
    private MyApplication myApp;

    //Firebase variables
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat_listing, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Inbox");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myApp = new MyApplication();

        chatList = rootView.findViewById(R.id.chatListRecyclerView);
        chatList.setHasFixedSize(true);
        dialog = new ProgressDialog(this.getContext());
        dialog.setMessage("Loading.");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        // Set up Layout Manager, reverse layout
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(layoutManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query chatQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(chatQuery, ChatMessage.class)
                .build();

        //Configure adapter
        chatAdapter = new ChatListingAdapter(options,this) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        };
        //Set divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(chatList.getContext(),
                layoutManager.getOrientation());
        chatList.addItemDecoration(dividerItemDecoration);

        //Set adapter
        chatList.setAdapter(chatAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (chatAdapter != null) {
            chatAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }
    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //TODO: Method necessary to tweak query in later stage (i.e. make this abstract class)
    // public abstract Query getQuery(DatabaseReference databaseReference);

    //Method can be placed in inherited class later on
    public Query getQuery(DatabaseReference databaseReference) {
        DatabaseReference chatDB = databaseReference.child("chatHistory");
        Query recentStoreQuery = chatDB.child(myApp.getUserName()).child("U Stars Supermarket").limitToFirst(100);
        // [END recent_store_query]
        return recentStoreQuery;
    }

}
