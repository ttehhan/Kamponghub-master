package com.example.hdb.kamponghub.models;

import java.util.Date;
import android.graphics.Bitmap;

/**
 * Created by TTH on 13/12/2017.
 */

public class ChatMessage {
    private String msg;
    private String email;
    private String createdAtTime;
    private String createdAtDate;
    private String sender;
    private boolean myMsg;
    private Bitmap image;


    public ChatMessage () {

    }

    public ChatMessage (String sender, Bitmap image, String createdAtDate, String createdAtTime, Boolean myMsg) {
        this.sender = sender;
        this.image = image;
        this.createdAtDate = createdAtDate;
        this.createdAtTime = createdAtTime;
        this.myMsg = myMsg;
    }

    public ChatMessage(String sender, String msg, String createdAtDate, String createdAtTime, Boolean myMsg) {
        this.sender = sender;
        this.msg = msg;
        this.createdAtDate = createdAtDate;
        this.createdAtTime = createdAtTime;
        this.myMsg = myMsg;
    }

    public String getSender() { return sender;}

    public void setSender(String sender) { this.sender = sender; }

    public String getMsg() {return msg;}

    public void setMsg(String msg) {this.msg = msg;
    }

    public String getDate() {return createdAtDate; }

    public void setDate(String date) {this.createdAtDate = date; }

    public String getTime() {return createdAtTime;
    }

    public void setTime(String time) {this.createdAtTime = time;
    }

    public boolean getMsgType() {return myMsg;}

    public void setMsgType(boolean msgType) {this.myMsg = msgType;}


    public Bitmap getImage() {return image;}

    public void setImage(Bitmap imageSelected) {this.image = imageSelected;
    }

    public String getEmail() {return email;
    }
    public void setEmail(String email) {this.email= email;
    }
}
