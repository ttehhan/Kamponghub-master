package com.example.hdb.kamponghub.models;

import java.util.Date;

/**
 * Created by TTH on 13/12/2017.
 */

public class ChatMessage {
    private String msg;
    private String email;
    private String createdAt;
    private boolean myMsg;


    public ChatMessage () {

    }

    public ChatMessage(String msg, String email, String createdAt, Boolean myMsg) {
        this.msg = msg;
        this.email = email;
        this.createdAt = createdAt;
        this.myMsg = myMsg;
    }

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

    public String getTime() {
        return createdAt;
    }

    public void setTime(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getMsgType() {return myMsg;}

    public void setMsgType(boolean msgType) {this.myMsg = msgType;}
}
