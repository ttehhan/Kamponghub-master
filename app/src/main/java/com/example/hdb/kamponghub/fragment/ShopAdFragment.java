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

import com.example.hdb.kamponghub.NavigationActivity;
import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.adapter.AdapterAdList;
import com.example.hdb.kamponghub.models.Advert;
import com.example.hdb.kamponghub.models.Shop;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * This is slightly different from ShopOwner ad: Will List ads given ShopID, ShopZone
 */
public class ShopAdFragment extends Fragment {

    //This constant is for easy referencing for Log purposes
    private static final String TAG = ShopAdFragment.class.getSimpleName();
    public static final String SHOP_KEY = "shop_ad_key";
    public static final String ZONE_KEY = "zone_key";

    //Layout
    private RecyclerView rvAdList;
    private LinearLayoutManager layoutManager;
    private AdapterAdList mFirebaseAdapter;
    private ProgressDialog dialog;

    //Firebase variables
    private DatabaseReference mDatabase;
    private String mShopKey;
    private String mZoneKey;

    //Model
    Shop shop;
    public ShopAdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_shop_ad, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Shop Ads");
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        rvAdList = rootView.findViewById(R.id.adListRecyclerView);
        rvAdList.setHasFixedSize(true);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading data.");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        // Get ad key from intent
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mShopKey = bundle.getString(SHOP_KEY, null);
            if (mShopKey == null) {
                throw new IllegalArgumentException("Must pass SHOP_KEY");
            }
            mZoneKey = bundle.getString(ZONE_KEY, null);
            if (mZoneKey == null) {
                throw new IllegalArgumentException("Must pass ZONE_KEY");
            }

        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up Layout Manager, reverse layout
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvAdList.setLayoutManager(layoutManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query adQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Advert>()
                .setQuery(adQuery, Advert.class)
                .build();

        //Configure adapter
        mFirebaseAdapter = new AdapterAdList(options,this) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        };
        //Set divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvAdList.getContext(),
                layoutManager.getOrientation());
        rvAdList.addItemDecoration(dividerItemDecoration);

        //Set adapter
        rvAdList.setAdapter(mFirebaseAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.stopListening();
        }
    }
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_store_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        final String userId = ((NavigationActivity)getActivity()).getUid();
        Query adQuery = databaseReference.child("shops").child(mZoneKey).child(mShopKey).child("ads")
                .limitToFirst(100);
        // [END recent_store_query]

        return adQuery;
    }
}
