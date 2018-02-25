package com.example.hdb.kamponghub.models;

import android.app.Application;

import com.example.hdb.kamponghub.utilities.Calculations;

/**
 * Created by TTH on 5/1/2018.
 */

public class User {
    private String FireUID;
    private String email;
    private String username;
    private int postal;
    private int phone;
    private String userZone;

    private User() {

    }

    public User (String email, String username, int postal) {
        this.email = email;
        this.username = username;
        this.postal = postal;
        this.userZone = Calculations.calculateZone(String.valueOf(postal));
    }

    public User (String email, String username, int address, int phone)
    {
        this.email = email;
        this.username = username;
        this.postal = address;
        this.phone = phone;
        this.userZone = Calculations.calculateZone(String.valueOf(postal));
    }

    //public String getFireUID() { return FireUID; }
   //public void setFireUID(String uid) { this.FireUID = uid; }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getPostal() {
        return postal;
    }
    public void setPostal(int address) {
        this.postal = address;
    }
    public int getPhone() {return phone;}
    public void setPhone(int phone) {this.phone = phone;}
    public String getUserZone() {
        return userZone;
    }
    public void setUserZone(String userZone) {
        this.userZone = userZone;
    }

}
