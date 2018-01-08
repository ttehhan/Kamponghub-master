package com.example.hdb.kamponghub.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.models.Shop;
import com.example.hdb.kamponghub.viewholder.ShopListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

//Uses in class FirebaseRecycleAdapter

public class ShopListingFragment extends Fragment {
    //This constant is for easy referencing for Log purposes
    private static final String TAG = ShopListingFragment.class.getSimpleName();

    //Layout
    private RecyclerView rvShopList;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Shop, ShopListHolder> mFirebaseAdapter;
    private ProgressDialog dialog;

    //Firebase variables
    private DatabaseReference mDatabase;
    private ChildEventListener mShopListener; //not used

    //Model
    Shop shop;

    //Empty constructor
    public ShopListingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_shop_listing, container, false);

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
    public void onActivityCreated(Bundle savedInstanceState) {
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
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Shop, ShopListHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
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
                        Fragment fragment= new ShopDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(ShopDetailFragment.SHOP_DETAIL_KEY, shopKey);
                        fragment.setArguments(bundle);
                        goFragment(fragment);
                    }
                });
                viewHolder.bindToList(model,new View.OnClickListener(){
                    @Override
                    public void onClick(View chatView) {
                        Toast.makeText(getActivity(),model.getShopName(),Toast.LENGTH_SHORT).show();
                    }
                });
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
}
