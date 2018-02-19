package com.example.hdb.kamponghub.models;

import java.util.Date;

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


    public ChatMessage () {

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

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email= email;
    }

    public String getDate() {return createdAtDate; }

    public void setDate(String date) {this.createdAtDate = date; }

    public String getTime() {
        return createdAtTime;
    }

    public void setTime(String time) {
        this.createdAtTime = time;
    }

    public boolean getMsgType() {return myMsg;}

    public void setMsgType(boolean msgType) {this.myMsg = msgType;}
}
