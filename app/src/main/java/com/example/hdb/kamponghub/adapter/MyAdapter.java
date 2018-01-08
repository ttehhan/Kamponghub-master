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
import com.example.hdb.kamponghub.models.Shop;
import com.example.hdb.kamponghub.viewholder.ShopListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by CSLee on 8/1/2018.
 */

public class MyAdapter extends FirebaseRecyclerAdapter<Shop, ShopListHolder> {
    Fragment fragment;

    public MyAdapter(@NonNull FirebaseRecyclerOptions<Shop> options) {
        super(options);
    }
    public MyAdapter(@NonNull FirebaseRecyclerOptions<Shop> options, Fragment fragment) {
        super(options);
        this.fragment=fragment;
    }
    @Override
    public ShopListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ShopListHolder(inflater.inflate(R.layout.shop_list_item, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(ShopListHolder viewHolder, int position, final Shop model) {
        //TODO: Method to be added later: To show store details
        final DatabaseReference shopRef = getRef(position);

        // Set click listener for the shop view
        final String shopKey = shopRef.getKey();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch ShopDetailActivity
                Fragment newFragment= new ShopDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ShopDetailFragment.SHOP_DETAIL_KEY, shopKey);
                newFragment.setArguments(bundle);
                ((NavigationActivity)fragment.getActivity()).goFragment(newFragment,R.id.screen_area);
            }
        });
        viewHolder.bindToList(model,new View.OnClickListener(){
            @Override
            public void onClick(View chatView) {
                Toast.makeText(fragment.getActivity(),model.getShopName(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
