package com.example.hdb.kamponghub.models;

import android.app.Application;

/**
 * Created by TTH on 5/1/2018.
 */

public class User {
    private String FireUID;
    private String email;
    private String username;
    private int address;
    private int phone;
    private String userZone;

    private User() {

    }

    public User (String email, String username, int address) {
        this.email = email;
        this.username = username;
        this.address = address;
    }

    public User (String email, String username, int address, int phone)
    {
        this.email = email;
        this.username = username;
        this.address = address;
        this.phone = phone;
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
    public int getAddress() {
        return address;
    }
    public void setAddress(int address) {
        this.address = address;
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
