package com.example.hdb.kamponghub;

import android.content.Intent;
import android.content.Context;
import android.Manifest;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.app.ProgressDialog;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.database.Cursor;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.content.DialogInterface;
import android.util.Base64;
import android.net.Uri;
import java.io.IOException;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.example.hdb.kamponghub.adapter.MessageAdapter;
import com.example.hdb.kamponghub.models.ChatMessage;
import com.example.hdb.kamponghub.models.MyApplication;
import com.example.hdb.kamponghub.models.User;
import com.example.hdb.kamponghub.utilities.Calculations;
import com.example.hdb.kamponghub.utilities.Permissions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {
    private Button sendMessage;
    private ImageView sendImage;
    private EditText input;
    private ActionBar actionBar;
    private ListView listView;
    private List<ChatMessage> chatMsgHistory;
    private ArrayAdapter<ChatMessage> adapter;
    boolean myMsg = true;
    private DatabaseReference rootDB, chatDB,shopBranch;
    private DatabaseReference latestMsgList; //stores only the latest message to display in chatListing
    private MyApplication myApp;
    private String userID;
    private ProgressDialog progressDialog;
    private boolean sentDoNotRefresh = false;
    private SimpleDateFormat mdformat;
    private static final int REQUEST_CAMERA = 1;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imageURI;
    //private FirebaseListAdapter<ChatMessage> adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myApp = (MyApplication) getApplicationContext(); //this is for sharing a variable across various fragment and activity
        userID = myApp.getUserName();

        rootDB = FirebaseDatabase.getInstance().getReference(); //this gets a reference of the root database
        Log.d("username", userID);
        chatDB = rootDB.child("chatHistory").child(userID);
        Log.d("shopname", myApp.getShopName());
        shopBranch = chatDB.child(myApp.getShopName());
        Query queryRef = chatDB.child(myApp.getShopName()).orderByKey(); //query from firebase to retrieve only chatMessages based on shop name

        chatMsgHistory = new ArrayList<>(); //this will store the messages sent out to firebase

        LinearLayoutManager verticalScroll = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //needed to set up the action bar to display the navigation back button to MainActivity
        listView = (ListView)findViewById(R.id.list_msg);
        sendMessage = (Button)findViewById(R.id.button_chatbox_send);
        sendImage = (ImageView)findViewById(R.id.upload_image);
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

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                sentDoNotRefresh = true;
            }
            });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage chatMessage = new ChatMessage(myApp.getShopName(), input.getText().toString(),getCurrentDate(), getCurrentTime(), true);
                chatMessage.setMsgType(true);
                chatMsgHistory.add(chatMessage);

                adapter.notifyDataSetChanged();
                //shopBranch.child(getCurrentDate()).push().setValue(chatMessage);
                shopBranch.push().setValue(chatMessage);

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
        mdformat = new SimpleDateFormat("hh:mm a");
        String strTime = mdformat.format(calendar.getTime());
        return strTime;
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("ddMMyyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public void selectImage() {
        final CharSequence[] items = { "Camera", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
        builder.setTitle("Send a photo via chat");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Permissions.checkPermission(Chat.this);
                if (items[item].equals("Camera")) {
                    if(result)
                        { //calls an implicit intent
                            Intent takeCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takeCamera, REQUEST_CAMERA);
                        }
                } else if (items[item].equals("Choose from Library")) {
                    if(result)
                    {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickPhoto.setType("image/*");
                        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(pickPhoto, "Select an image"), PICK_IMAGE_REQUEST);
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK  && data != null && data.getData() != null )
        {
                imageURI = data.getData();
            try {
                adapter = new MessageAdapter(this, R.layout.message_sent, chatMsgHistory);
                listView.setAdapter(adapter);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 00, 200, false);
                storeImageToFirebase(resizedBitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void storeImageToFirebase(Bitmap imageSelected) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageSelected.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
        // we finally have our base64 string version of the image, save it.
        ChatMessage chatImage = new ChatMessage(myApp.getShopName(), true, getCurrentDate(), getCurrentTime(), base64Image);
        chatMsgHistory.add(chatImage);
        adapter.notifyDataSetChanged();
        shopBranch.push().setValue(chatImage);
    }


}
