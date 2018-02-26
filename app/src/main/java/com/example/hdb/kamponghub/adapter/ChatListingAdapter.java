package com.example.hdb.kamponghub.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hdb.kamponghub.NavigationActivity;
import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.fragment.ShopDetailFragment;
import com.example.hdb.kamponghub.models.ChatMessage;
import com.example.hdb.kamponghub.models.Shop;
import com.example.hdb.kamponghub.viewholder.ChatListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by TTH on 25/2/2018.
 */

public class ChatListingAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatListHolder> {
   private Fragment fragment;

    public ChatListingAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options) {super(options);}
    public ChatListingAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options, Fragment fragment) {
        super(options);
        this.fragment=fragment;
         }
    @Override
    public ChatListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ChatListHolder(inflater.inflate(R.layout.chat_list_item, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(ChatListHolder viewHolder, int position, final ChatMessage msg) {

        final DatabaseReference shopRef = getRef(position);

        // Set click listener for the shop view

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Fragment newFragment= new ShopDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ShopDetailFragment.SHOP_DETAIL_KEY, shopKey);
                bundle.putString(ShopDetailFragment.SHOP_ZONE_KEY, shopZone);
                newFragment.setArguments(bundle);
                ((NavigationActivity)fragment.getActivity()).goFragment(newFragment,R.id.screen_area);*/
            }
        });
        viewHolder.bindToList(msg,new View.OnClickListener(){
            @Override
            public void onClick(View chatView) {

            }
        });
    }
  }
