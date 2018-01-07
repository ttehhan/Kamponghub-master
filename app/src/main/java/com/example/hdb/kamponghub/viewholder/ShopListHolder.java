package com.example.hdb.kamponghub.viewholder;

/**
 * Created by CSLee on 23/12/2017.
 */
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.models.Shop;
import com.squareup.picasso.Picasso;

//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

public class ShopListHolder extends RecyclerView.ViewHolder{
    private static final String TAG = ShopListHolder.class.getSimpleName();
    public TextView shopName;
    public TextView shopIsOpen;
    public TextView shopTime;
    public TextView shopDistance;
    public ImageView shopImage;


    public ShopListHolder(View itemView) {
        super(itemView);
       shopImage = (ImageView)itemView.findViewById(R.id.shopImage);
        shopName = (TextView)itemView.findViewById(R.id.shopName);
        shopIsOpen = (TextView)itemView.findViewById(R.id.isShopOpen);
        shopTime = (TextView)itemView.findViewById(R.id.shopTime);
        shopDistance = (TextView)itemView.findViewById(R.id.shopDistance);

    }
    public void bindToList(Shop shop,View.OnClickListener chatClickListener) {
        setShopName(shop.getShopname());
        setImage(shop.getShopImageUrl());

       setShopOpen("T1","T2");
       setTime("T1","T2");
        setDistance("A","B");

    }
    public void setShopName(String title)
    {
        shopName.setText(title);
    }

    public void setImage(String imageUrl)
    {

        Picasso.with(itemView.getContext())
                .load(imageUrl)
                .into(shopImage);
    }
    public void setShopOpen(String timeStart, String timeEnd){
        //TODO: Calculate is shop open
        //Parse string to Integer and calculate
        shopIsOpen.setText("Open");
    }
    public void setTime(String timeStart, String timeEnd){
        //TODO: Calculate time span
        //Parse string to Integer and calculate
        shopTime.setText("9am to 9pm");
    }
    public void setDistance(String currentDistance, String shopCoordinates){
        //TODO: Need to calculate distance based on current coordinates and shop coordinates
        shopDistance.setText("1km");
    }
}
