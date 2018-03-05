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
    private String image;
    private String receiverName;


    public ChatMessage () {

    }

    public ChatMessage (String sender, String receiverName, Boolean myMsg, String createdAtDate, String createdAtTime, String image) {
        this.sender = sender;
        this.receiverName = receiverName;
        this.image = image;
        this.createdAtDate = createdAtDate;
        this.createdAtTime = createdAtTime;
        this.myMsg = myMsg;
    }

    public ChatMessage(String sender, String receiverName, String msg, String createdAtDate, String createdAtTime, Boolean myMsg) {
        this.sender = sender;
        this.receiverName = receiverName;
        this.msg = msg;
        this.createdAtDate = createdAtDate;
        this.createdAtTime = createdAtTime;
        this.myMsg = myMsg;
    }

    public String getSender() { return sender;}

    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiverName;}

    public void setReceiver(String receiverName) { this.receiverName = receiverName; }

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


    public String getImage() {return image;}

    public void setImage(String imageSelected) {this.image = imageSelected;
    }

    public String getEmail() {return email;
    }
    public void setEmail(String email) {this.email= email;
    }
}
