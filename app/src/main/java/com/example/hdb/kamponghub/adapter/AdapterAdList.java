package com.example.hdb.kamponghub.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hdb.kamponghub.NavigationActivity;
import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.fragment.ShopAdDetailFragment;
import com.example.hdb.kamponghub.models.Advert;
import com.example.hdb.kamponghub.viewholder.AdListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by CSLee on 8/1/2018.
 */

public class AdapterAdList extends FirebaseRecyclerAdapter<Advert, AdListHolder> {
   private Fragment fragment;

    public AdapterAdList(@NonNull FirebaseRecyclerOptions<Advert> options) {
        super(options);
    }
    public AdapterAdList(@NonNull FirebaseRecyclerOptions<Advert> options, Fragment fragment) {
        super(options);
        this.fragment=fragment;
         }
    @Override
    public AdListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new AdListHolder(inflater.inflate(R.layout.ad_list_item, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(AdListHolder viewHolder, int position, final Advert model) {
        //TODO: Method to be added later: To show store details
        final DatabaseReference adRef = getRef(position);

        // Set click listener for the ad view
        final String adKey = adRef.getKey();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass in ShopZone and ShopID
                String shopId = model.getShopId();
                String shopZone = model.getShopZone();
                // Launch AdDetailFragment
                Fragment newFragment= new ShopAdDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ShopAdDetailFragment.SHOP_AD_KEY, adKey);
                bundle.putString(ShopAdDetailFragment.SHOP_ID_KEY, shopId);
                bundle.putString(ShopAdDetailFragment.SHOP_ZONE_KEY, shopZone);
                newFragment.setArguments(bundle);
                ((NavigationActivity)fragment.getActivity()).goFragment(newFragment,R.id.screen_area);
            }
        });
        viewHolder.bindToList(model);
    }
  }
