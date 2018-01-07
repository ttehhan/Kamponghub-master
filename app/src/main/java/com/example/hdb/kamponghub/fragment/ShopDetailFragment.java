package com.example.hdb.kamponghub.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.models.Shop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ShopDetailFragment extends Fragment{
    //Constants are for easy referencing for Log purposes
    private static final String TAG = ShopDetailFragment.class.getSimpleName();
    public static final String SHOP_DETAIL_KEY = "shop_detail_key";

    //Layout
    private ImageView shopImage;
    private TextView shopName;
    private TextView isShopOpen;
    private TextView shopTime;
    private TextView shopAddress;
    private Fragment fragment;
    private ProgressDialog dialog;

    //Firebase variables
    private DatabaseReference mShopReference;
    private String mShopKey;


    //Model
    Shop shop;

    //Empty constructor
    public ShopDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_shop_detail, container, false);
        // Get shop key from intent
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mShopKey = bundle.getString(SHOP_DETAIL_KEY, null);
            if (mShopKey == null) {
                throw new IllegalArgumentException("Must pass SHOP_DETAIL_KEY");
            }
        }

        // Initialize Database
        mShopReference = FirebaseDatabase.getInstance().getReference()
                .child("shops").child(mShopKey);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading data.");
        dialog.show();


        // Initialize Views
        shopImage = rootView.findViewById(R.id.shopImage);
        shopName = rootView.findViewById(R.id.shopName);
        isShopOpen = rootView.findViewById(R.id.isShopOpen);
        shopTime = rootView.findViewById(R.id.shopTime);
        shopAddress=rootView.findViewById(R.id.shopAddress);


        //Get data
        // Attach a listener to read the data at shops reference
        mShopReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 Shop shop = dataSnapshot.getValue(Shop.class);
                  setImage(shop.getShopImageUrl());
                 shopName.setText(shop.getShopname());
                isShopOpen.setText("Function to calculate");
                shopTime.setText("Function to calculate");
                  shopAddress.setText(shop.getShopAddress());
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Shop failed, log a message
                Log.w(TAG, "loadShop:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getContext(), "Failed to load shop details.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
               return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         fragment = new MapsFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.childFragmentContainer, fragment).commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //TODO: Method necessary to tweak query in later stage (i.e. make this abstract class)
   // public abstract Query getQuery(DatabaseReference databaseReference);

    //Method can be placed in inherited class later on
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_store_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentStoreQuery = databaseReference.child("shops")
                .limitToFirst(100);
        // [END recent_store_query]

        return recentStoreQuery;
    }

    private void goFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //For replace: refers to the FrameLayout in "content_main"
        ft.replace(R.id.screen_area,fragment)
                .addToBackStack(null)
                .commit();
    }

    public void setImage(String imageUrl)
    {
        Picasso.with(this.getContext())
                .load(imageUrl)
                .into(shopImage);
    }

    }
