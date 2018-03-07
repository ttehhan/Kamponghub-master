package com.example.hdb.kamponghub.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hdb.kamponghub.Chat;
import com.example.hdb.kamponghub.NavigationActivity;
import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.fragment.ChatListingFragment;
import com.example.hdb.kamponghub.models.ChatMessage;
import com.example.hdb.kamponghub.viewholder.ChatListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import com.example.hdb.kamponghub.models.MyApplication;

/**
 * Created by TTH on 25/2/2018.
 */

public class ChatListingAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatListHolder> {
   private Fragment fragment;
   private MyApplication myApp;


    public ChatListingAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options) {super(options);}
    public ChatListingAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options, Fragment fragment, List<ChatMessage> chatList) {
        super(options);
        this.fragment = fragment;
        myApp = (MyApplication) fragment.getActivity().getApplicationContext();
         }
    @Override
    public ChatListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ChatListHolder(inflater.inflate(R.layout.chat_list_item, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(final ChatListHolder viewHolder, int position, ChatMessage msg) {

        final DatabaseReference shopRef = getRef(position);
        //final String sender = msg.getSender();
        //final String ShopKey = shopRef.getKey();


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myApp.setShopID(ShopKey);
                //Log.d("valerie", ShopKey);
                Intent i = new Intent((fragment.getActivity()).getApplicationContext(),Chat.class);
                fragment.startActivity(i);

            }
        });
        viewHolder.bindToList(msg,new View.OnClickListener(){
            @Override
            public void onClick(View chatView) {

            }
        });
    }
  }
