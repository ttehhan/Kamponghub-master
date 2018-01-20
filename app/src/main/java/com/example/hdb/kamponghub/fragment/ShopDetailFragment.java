package com.example.hdb.kamponghub.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdb.kamponghub.NavigationActivity;
import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.models.Shop;
import com.example.hdb.kamponghub.utilities.Calculations;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


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
    private ImageButton imgBtnPhone;
    private ImageButton imgBtnBookmark;

    //Firebase variables
    private DatabaseReference mShopReference;
    private DatabaseReference mLikeReference;

    private String mShopKey;
    private String shopLatitude;
    private  String shopLongtitude;
   private String phone;
    //Model
    Shop shop;

    //Empty constructor
    public ShopDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_shop_detail, container, false);

        //Set progress dialog
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading data.");
        dialog.show();

        // Get shop key from intent
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mShopKey = bundle.getString(SHOP_DETAIL_KEY, null);
            if (mShopKey == null) {
                throw new IllegalArgumentException("Must pass SHOP_DETAIL_KEY");
            }
        }
        final String userId = ((NavigationActivity)getActivity()).getUid();
        // Initialize Database
        mShopReference = FirebaseDatabase.getInstance().getReference()
                .child("shops").child(mShopKey);
        mLikeReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("shopsLiked");

        // Initialize Views
        shopImage = rootView.findViewById(R.id.shopImage);
        shopName = rootView.findViewById(R.id.shopName);
        isShopOpen = rootView.findViewById(R.id.isShopOpen);
        shopTime = rootView.findViewById(R.id.shopTime);
        shopAddress=rootView.findViewById(R.id.shopAddress);
        imgBtnPhone=rootView.findViewById(R.id.imgBtnPhone);
        imgBtnBookmark=rootView.findViewById(R.id.imgBtnBookmark);
        //Go phone on imgBtnPhone click
        imgBtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch dialer
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel: +65"+phone));
                startActivity(callIntent);
            }
        });
        //Like or unlike shop
        imgBtnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update firebase by toggling status
                if (((String) imgBtnBookmark.getTag())=="isLiked"){
                    //Need to unlike
                     mLikeReference.child(mShopKey).removeValue();
                }else{
                    //Need to like
                    if (shop!=null) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(mShopKey, shop);
                        mLikeReference.updateChildren(childUpdates);
                    }
                }

            }
        });
        //Get data
        // Attach a listener to read the data at shops reference
        mShopReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 shop = dataSnapshot.getValue(Shop.class);
                setImage(shop.getShopImageUrl());
                shopName.setText(shop.getShopName());
                isShopOpen.setText(Calculations.calcShopOpen(shop.getTimeStart(),shop.getTimeEnd(),"1200"));
                shopTime.setText(Calculations.calcTime(shop.getTimeStart(),shop.getTimeEnd()));
                shopAddress.setText(shop.getShopAddress());
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                shopLatitude = shop.getShopLatitude();
                shopLongtitude = shop.getShopLongitude();
                //Get phone number from database
                phone = shop.getPhoneNumber().toString();

                fragment = new MapsFragment();

                if (shopLatitude!=null && shopLongtitude!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString(MapsFragment.SHOP_LATITUDE_KEY, shopLatitude);
                    bundle.putString(MapsFragment.SHOP_LONGTITUDE_KEY,shopLongtitude);
                    fragment.setArguments(bundle);
                }

                goChildFragment(fragment,R.id.childFragmentContainer);
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

        //Attach listener to check if user like shop
        mLikeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Determine if user liked shop
                if (dataSnapshot.hasChild(mShopKey)) {
                   imgBtnBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    imgBtnBookmark.setTag("isLiked");

                }else{
                    imgBtnBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    imgBtnBookmark.setTag("notLiked");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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


    public void setImage(String imageUrl)
    {
        Picasso.with(this.getContext())
                .load(imageUrl)
                .into(shopImage);
    }

    public void goChildFragment(Fragment fragment, int toReplace){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(toReplace, fragment).commit();
    }

}
