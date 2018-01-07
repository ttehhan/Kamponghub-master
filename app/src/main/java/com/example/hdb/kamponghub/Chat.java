package com.example.hdb.kamponghub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {
    private Button sendMessage;
    private EditText input;
    private ActionBar actionBar;
    private ListView listView;
    private List<ChatMessage> chatMsgHistory;
    private ArrayAdapter<ChatMessage> adapter;
    boolean myMsg = true;
    private DatabaseReference rootDB, chatDB;
    private String UserEmail;
    //private FirebaseListAdapter<ChatMessage> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle extras = getIntent().getExtras();
        UserEmail = extras.getString("email");
        rootDB = FirebaseDatabase.getInstance().getReference(); //this gets a reference of the root database
        chatDB = rootDB.child("chatHistory");
        Query queryRef = chatDB.orderByChild("email").equalTo(UserEmail); //query from firebase to retrieve only chatMessages of logged in user
        chatMsgHistory = new ArrayList<>(); //this will store the messages sent out to firebase
        LinearLayoutManager verticalScroll = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //needed to set up the action bar to display the navigation back button to MainActivity
        listView = (ListView)findViewById(R.id.list_msg);
        sendMessage = (Button)findViewById(R.id.button_chatbox_send);
        input = (EditText)findViewById(R.id.edittext_chatbox);

        adapter = new MessageAdapter(this, R.layout.message_sent, chatMsgHistory);
        listView.setAdapter(adapter);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chatDataSnapshot : dataSnapshot.getChildren()) {
                    //Object chatDetails = chatDataSnapshot.getValue();
                    //Log.d("natalia:", chatDetails.toString());
                    ChatMessage chatMsg = chatDataSnapshot.getValue(ChatMessage.class);
                    //Log.d("natalia", chatMsg.getMsg());
                    chatMsg.setMsgType(myMsg); //harcoded to set to my message as of now
                    chatMsgHistory.add(chatMsg);

                    //if ( chatMsg.getEmail() == UserEmail) {
                      //  chatMsg.setMsgType(true);
                    //} else {
                      //  chatMsg.setMsgType(false);
                    //}
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //List<ChatMessage> chatMessages = new ArrayList<>();
                ChatMessage chatMessage = new ChatMessage(input.getText().toString(), UserEmail, getCurrentTime(), myMsg);
                chatMsgHistory.add(chatMessage);
                adapter.notifyDataSetChanged();
                chatDB.push().setValue(chatMessage);

                input.setText("");
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
        Intent myIntent = new Intent(getApplicationContext(), HomePage.class);
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
