package com.example.hdb.kamponghub.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.adapter.AdapterShopList;
import com.example.hdb.kamponghub.models.Shop;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

//Like ShopListingFragment but restricted to shops liked
public class BookMarkFragment extends Fragment {
    //This constant is for easy referencing for Log purposes
    private static final String TAG = BookMarkFragment.class.getSimpleName();
    //Layout
    private RecyclerView rvShopList;
    private LinearLayoutManager layoutManager;
    private AdapterShopList mFirebaseAdapter;
    private ProgressDialog dialog;

    //Firebase variables
    private DatabaseReference mDatabase;

    //Model
    Shop shop;

    public BookMarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_shop_listing, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Bookmarks");
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        rvShopList = rootView.findViewById(R.id.shopListRecyclerView);
       rvShopList.setHasFixedSize(true);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading data.");
        dialog.show();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up Layout Manager, reverse layout
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvShopList.setLayoutManager(layoutManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query shopQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Shop>()
                .setQuery(shopQuery, Shop.class)
                .build();

        //Configure adapter
        mFirebaseAdapter = new AdapterShopList(options,this) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        };
        //Set divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvShopList.getContext(),
                layoutManager.getOrientation());
        rvShopList.addItemDecoration(dividerItemDecoration);

        //Set adapter
        rvShopList.setAdapter(mFirebaseAdapter);
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
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    //Method can be placed in inherited class later on
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_store_query]
        // Get shops liked
        Query shopsLikeQuery = databaseReference.child("users").child(getUid()).child("shopsLiked");

        // [END recent_store_query]

        return shopsLikeQuery;
    }
}
