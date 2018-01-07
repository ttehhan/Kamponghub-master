package com.example.hdb.kamponghub.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Shop {



    private String shopName;

    private String shopAddress;
    private  String shopImageUrl;

    public Shop() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Shop(String shopname, String shopImageUrl,String address) {
        this.shopName= shopname;
        this.shopImageUrl = shopImageUrl;
        this.shopAddress=address;
            }

    public String getShopname() {
        return shopName;
    }

    public void setShopname(String shopname) {
        this.shopName = shopname;
    }

    public String getShopImageUrl() {
        return shopImageUrl;
    }

    public void setShopImageUrl(String shopImageUrl) {
        this.shopImageUrl = shopImageUrl;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String address) {
        this.shopAddress = address;
    }

}
// [END comment_class]

