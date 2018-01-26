package com.example.hdb.kamponghub.models;

import android.app.Application;

/**
 * Singleton class to hold data Created by TTH on 22/1/2018.
 */

public class MyApplication extends Application {

    public MyApplication()
    {}

    private static String UserEmail;

    public String getEmail()
    {return UserEmail;}

    public void setEmail(String email)
    {this.UserEmail = email;}
}
