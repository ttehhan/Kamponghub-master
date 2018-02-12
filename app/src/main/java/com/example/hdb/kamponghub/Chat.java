package com.example.hdb.kamponghub;

import android.content.Intent;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Button;
import android.app.ProgressDialog;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.lang.Object;

import com.example.hdb.kamponghub.adapter.MessageAdapter;
import com.example.hdb.kamponghub.models.ChatMessage;
import com.example.hdb.kamponghub.models.MyApplication;
import com.example.hdb.kamponghub.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.example.hdb.kamponghub.models.MyApplication;

public class Chat extends AppCompatActivity {
    private Button sendMessage;
    private EditText input;
    private ActionBar actionBar;
    private ListView listView;
    private List<ChatMessage> chatMsgHistory;
    private ArrayAdapter<ChatMessage> adapter;
    boolean myMsg = true;
    private DatabaseReference rootDB, chatDB;
    private MyApplication myApp;
    private String username;
    private ProgressDialog progressDialog;
    private boolean sentDoNotRefresh = false;
    //private FirebaseListAdapter<ChatMessage> adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myApp = (MyApplication) getApplicationContext(); //this is for sharing a variable across various fragment and activity
        username = myApp.getUserName();
        rootDB = FirebaseDatabase.getInstance().getReference(); //this gets a reference of the root database
        Log.d("username", username);
        chatDB = rootDB.child("chatHistory").child(username);
        Log.d("shopname", myApp.getShopName());
        Query queryRef = chatDB.child(myApp.getShopName()).orderByKey(); //query from firebase to retrieve only chatMessages based on shop name
        chatMsgHistory = new ArrayList<>(); //this will store the messages sent out to firebase

        LinearLayoutManager verticalScroll = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //needed to set up the action bar to display the navigation back button to MainActivity
        listView = (ListView)findViewById(R.id.list_msg);
        sendMessage = (Button)findViewById(R.id.button_chatbox_send);
        input = (EditText)findViewById(R.id.edittext_chatbox);

        adapter = new MessageAdapter(this, R.layout.message_sent, chatMsgHistory);
        listView.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!sentDoNotRefresh) {
                    for (DataSnapshot chatDataSnapshot : dataSnapshot.getChildren()) {
                        ChatMessage chatMsg = chatDataSnapshot.getValue(ChatMessage.class);
                        chatMsgHistory.add(chatMsg);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage chatMessage = new ChatMessage(input.getText().toString(), myApp.getEmail(), getCurrentTime(), true);
                chatMessage.setMsgType(true);
                chatMsgHistory.add(chatMessage);

                adapter.notifyDataSetChanged();
                chatDB.child(myApp.getShopName()).push().setValue(chatMessage);
                //chatDB.push().setValue(chatMessage);

                input.setText("");
                sentDoNotRefresh = true; //need to set this to prevent onDataChange from populating data once send message is clicked

            }
        });


        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(view);
                input.clearFocus();
                return false;
            }
        });

    }
    //method to create a navigation back button back to the MainActivity
    public boolean onOptionsItemSelected(MenuItem item){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Intent myIntent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
    //method to hide the keyboard after touch on listView
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm a");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }
}
