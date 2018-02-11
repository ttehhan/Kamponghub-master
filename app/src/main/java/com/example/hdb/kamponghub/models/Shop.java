package com.example.hdb.kamponghub.models;

import android.content.Context;

import com.example.hdb.kamponghub.utilities.Calculations;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START comment_class]
@IgnoreExtraProperties
public class Shop {


    private String shopOwnerUid;
    private String shopName;
    //This is Base64 string to allow user to upload own image
    private String shopImage;
    private String shopAddress;
    private String shopPostal;
    private String timeEnd;
    private String timeStart;
    private Long phoneNumber;
    private String shopDescription;


    //Calculated
    private String shopLatitude;
    private String shopLongitude;
    private String shopZone;



    public Shop() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Shop(String shopOwnerUid, String shopname, String shopImage,
                String address, String postal,
                String timeEnd, String timeStart, String shopDescription,
                String phoneNumber, Context context) {
        this.shopOwnerUid= shopOwnerUid;
        this.shopName= shopname;
        this.shopImage = shopImage;
        this.shopAddress=address;
        this.timeEnd=timeEnd;
        this.timeStart=timeStart;
        this.shopPostal = postal;
        this.shopDescription= shopDescription;
        this.phoneNumber=Long.parseLong(phoneNumber);

        //Calculated
        this.shopLatitude= String.valueOf(Calculations.getLatLngFromPostal(context,postal).latitude);
        this.shopLongitude=String.valueOf(Calculations.getLatLngFromPostal(context,postal).longitude);
        this.shopZone = Calculations.calculateZone(postal);

/*        this.shopLatitude= shopLatitude;
        this.shopLongitude=shopLongitude;
        this.shopZone ="WEST";
        */
    }


    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getShopAddress() {
        return shopAddress;
    }
    public void setShopAddress(String address) {
        this.shopAddress = address;
    }
    public String getShopLatitude() {
        return shopLatitude;
    }
    public void setShopLatitude(String shopLatitude) {
        this.shopLatitude = shopLatitude;
    }
    public String getShopLongitude() {
        return shopLongitude;
    }
    public void setShopLongitude(String shopLongitude) {
        this.shopLongitude = shopLongitude;
    }
    public String getTimeEnd() {
        return timeEnd;
    }
    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
    public String getTimeStart() {
        return timeStart;
    }
    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }
    public String getShopImage() {
        return shopImage;
    }
    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }
    public String getShopOwnerUid() {
        return shopOwnerUid;
    }
    public void setShopOwnerUid(String shopOwnerUid) {
        this.shopOwnerUid = shopOwnerUid;
    }
    public String getShopPostal() {
        return shopPostal;
    }
    public void setShopPostal(String shopPostal) {
        this.shopPostal = shopPostal;
    }
    public String getShopZone() {
        return shopZone;
    }
    public void setShopZone(String shopZone) {
        this.shopZone = shopZone;
    }
    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }
    // "toMap" is to put the whole object as Hashmap in Firebase child branch ("Shops")
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("shopOwnerUid", shopOwnerUid);
        result.put("shopName", shopName);
        result.put("shopImage", shopImage);
        result.put("shopAddress", shopAddress);
        result.put("shopPostal", shopPostal);
        result.put("shopLatitude", shopLatitude);
        result.put("shopLongitude", shopLongitude);
        result.put("timeEnd", timeEnd);
        result.put("timeStart", timeStart);
        result.put("shopDescription", shopDescription);
        result.put("shopZone", shopZone);
        result.put("phoneNumber", phoneNumber);
        return result;
    }

    //Old code
    //This was link to image stored in Firebase Storage in 1st version of program
   /* private String shopImageUrl;

    public String getShopImageUrl() {
        return shopImageUrl;
    }

    public void setShopImageUrl(String shopImageUrl) {
        this.shopImageUrl = shopImageUrl;
    }*/

    //For testing: Constructor to take in only ShopName, ShopImage. Other values are hardcoded
/*    public Shop(String shopOwnerUid, String shopname, String shopImage) {

        this.shopOwnerUid=shopOwnerUid;
        this.shopName= shopname;
        this.shopImage = shopImage;
        this.shopAddress="Blk 28";
        this.shopLatitude= "1.3333301";
        this.shopLongitude="103.74329650000004";
        this.timeEnd="0000";
        this.timeStart="0000";
    }*/

}
// [END comment_class]

