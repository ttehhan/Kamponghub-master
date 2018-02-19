package com.example.hdb.kamponghub.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdb.kamponghub.Chat;
import com.example.hdb.kamponghub.NavigationActivity;
import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.models.MyApplication;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import java.util.Locale;

import java.util.HashMap;
import java.util.Map;


public class ShopDetailFragment extends Fragment implements LocationListener {
    //Constants are for easy referencing for Log purposes
    private static final String TAG = ShopDetailFragment.class.getSimpleName();
    public static final String SHOP_DETAIL_KEY = "shop_detail_key";
    public static final String SHOP_ZONE_KEY = "shop_zone_key";

    //Layout
    private ImageView shopImage;
    private TextView shopName;
    private TextView isShopOpen;
    private TextView shopTime;
    private TextView shopAddress;
    private TextView shopPhone;
    private TextView shopDescription;
    private Fragment fragment;
    private ProgressDialog dialog;
    private Button btnPhone;
    private Button btnBookmark;
    private Button btnRoute;
    private Button btnChat;

    //Firebase variables
    private DatabaseReference mShopReference;
    private DatabaseReference mLikeReference;

    private String mShopKey;
    private String mZoneKey;
    private String shopLatitude, shopLongtitude;
    private String myLatitude, myLongtitude;
    private String phone;
    private MyApplication myApp;

    //Model
    Shop shop;
    LocationManager locationManager;
    Location loc;


    //Empty constructor
    public ShopDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_shop_detail, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Shop Detail");

        //init the myApp shared object
        myApp = (MyApplication) getActivity().getApplicationContext();
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
            mZoneKey = bundle.getString(SHOP_ZONE_KEY, null);
            if (mZoneKey == null) {
                throw new IllegalArgumentException("Must pass SHOP_ZONE_KEY");
            }
        }
        final String userId = ((NavigationActivity)getActivity()).getUid();
        // Initialize Database
        mShopReference = FirebaseDatabase.getInstance().getReference()
                .child("shops").child(mZoneKey).child(mShopKey);
        mLikeReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("shopsLiked");

        // Initialize Views
        shopImage = rootView.findViewById(R.id.shopImage);
        shopName = rootView.findViewById(R.id.shopName);
        isShopOpen = rootView.findViewById(R.id.isShopOpen);
        shopTime = rootView.findViewById(R.id.shopTime);
        shopPhone = rootView.findViewById(R.id.shopPhone);
        shopDescription=rootView.findViewById(R.id.shopDescription);
        shopAddress=rootView.findViewById(R.id.shopAddress);
        btnPhone=rootView.findViewById(R.id.btnPhone);
        btnBookmark=rootView.findViewById(R.id.btnBookmark);
        btnRoute =  rootView.findViewById(R.id.btnRoute);
        btnChat = rootView.findViewById(R.id.btnChat);

        //Go phone on imgBtnPhone click
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch dialer
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel: +65"+phone));
                startActivity(callIntent);
            }
        });

        //adds shop to bookmark list
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update firebase by toggling status
                if (((String) btnBookmark.getTag())=="isLiked"){
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

        btnChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //myApp.setShopName(mShopKey);
                myApp.setShopName(shop.getShopName());
                Intent i = new Intent(getActivity().getApplicationContext(),Chat.class);
                startActivity(i);
            }
            });

        //launches google map with origin set to your location to shop destination
        btnRoute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                }
                getLocation();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                        .appendQueryParameter("origin",myLatitude+","+myLongtitude)
                        .appendQueryParameter("destination",shopLatitude+ "," +shopLongtitude)
                        .appendQueryParameter("travelmode","transit");
                String url = builder.build().toString();
                Log.d("d",url);
                Intent ii = new Intent(Intent.ACTION_VIEW);
                ii.setData(Uri.parse(url));
                startActivity(ii);

            }
        });

        //Get data
        // Attach a listener to read the data at shops reference
        mShopReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 shop = dataSnapshot.getValue(Shop.class);
                shopImage.setImageBitmap(Calculations.base64ToBitmap(shop.getShopImage()));
                /*setImage(shop.getShopImageUrl());*/
                shopName.setText(shop.getShopName());
                isShopOpen.setText(Calculations.calcShopOpen(shop.getTimeStart(),shop.getTimeEnd(),"1200"));
                shopTime.setText(Calculations.calcTime(shop.getTimeStart(),shop.getTimeEnd()));
                shopAddress.setText(shop.getShopAddress());
                shopPhone.setText(shop.getPhoneNumber().toString());
                shopDescription.setText(shop.getShopDescription());
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                shopLatitude = shop.getShopLatitude();
                shopLongtitude = shop.getShopLongitude();
                //Get phone number from database
                phone = shop.getPhoneNumber().toString();

               /* fragment = new MapsFragment();
                if (shopLatitude!=null && shopLongtitude!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString(MapsFragment.SHOP_LATITUDE_KEY, shopLatitude);
                    bundle.putString(MapsFragment.SHOP_LONGTITUDE_KEY,shopLongtitude);
                    fragment.setArguments(bundle);
                }
                goChildFragment(fragment,R.id.childFragmentContainer);*/
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
                   btnBookmark.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                    btnBookmark.setTag("isLiked");

                }else{
                    btnBookmark.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                    btnBookmark.setTag("notLiked");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }

    //method to get users location via lat and long
    public void getLocation() {
        try{
        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
         if (locationManager != null) {
            loc = locationManager.getLastKnownLocation(bestProvider);
            }
        locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);
            if (loc != null) {
            myLatitude = String.valueOf(loc.getLatitude());
            myLongtitude = String.valueOf(loc.getLongitude());
            }
        } catch (SecurityException e) {    }
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
/*
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_store_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentStoreQuery = databaseReference.child("shops")
                .limitToFirst(100);
        // [END recent_store_query]

        return recentStoreQuery;
    }
*/


 /*   public void setImage(String imageUrl)
    {
        Picasso.with(this.getContext())
                .load(imageUrl)
                .into(shopImage);
    }*/

/*    public void goChildFragment(Fragment fragment, int toReplace){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(toReplace, fragment).commit();
    }*/

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = String.valueOf(location.getLatitude());
        myLongtitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this.getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
