package com.example.hdb.kamponghub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity; //make sures that it is backward compatible
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;

import com.example.hdb.kamponghub.models.MyApplication;
import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FacebookAuthProvider;

import com.example.hdb.kamponghub.models.MyApplication;
import com.example.hdb.kamponghub.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private EditText emailText, passwordText;
    private Button loginBtn, registerBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String email, password;
    private MyApplication myApp;
    private DatabaseReference userDB;
    //Have another reference for logged in user
    private DatabaseReference loginUser;

    private TextView forgetPass;

    private String userZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp = (MyApplication) getApplicationContext();
        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        registerBtn = (Button) findViewById(R.id.register);
        loginBtn = (Button) findViewById(R.id.login);
        forgetPass = (TextView)findViewById(R.id.forgot_password);
        callbackManager = CallbackManager.Factory.create();
        LoginButton FBloginBtn = (LoginButton) findViewById(R.id.fb_login);
        FBloginBtn.setReadPermissions("email", "public_profile");
        getLoginDetails(FBloginBtn);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance(); //gets the firebase auth object
        userDB = FirebaseDatabase.getInstance().getReference().child("users");



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Register.class);
                startActivity(i);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                loginRequest();
            }
    });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = "tester1@test.com";

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this,"Password recovery mail sent",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });



    }

    //method to login user using email and password
    protected void loginRequest() {
       email = emailText.getText().toString().trim();
       password  = passwordText.getText().toString().trim();

       //For Test
        if(TextUtils.isEmpty(email) && (TextUtils.isEmpty(password)))
        {
            email = "tester1@test.com";
            password ="123456";
        }
        myApp.setEmail(email);
        userDB.orderByChild("email").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User userData = dataSnapshot.getValue(User.class);
                myApp.setUserName(userData.getUsername());

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        /*if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter a valid email address",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Password cannot be empty!",Toast.LENGTH_LONG).show();
            return;
        }*/

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //start the profile activity
                        String userUid = firebaseAuth.getCurrentUser().getUid();

                       // progressDialog.dismiss();
                        //if the task is successful
                        if (task.isSuccessful()) {

                            myApp.setUID(userUid);
                            //Get user Zone to save in Shared Preference
                            loginUser= FirebaseDatabase.getInstance().getReference().child("users").child(userUid);
                            loginUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    SharedPreferences sharedPref = getSharedPreferences("USERZONE",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("userZone", user.getUserZone());
                                    editor.commit();

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Shop failed, log a message
                                    Log.w("MAINACTIVITY", "saveUser:onCancelled", databaseError.toException());
                                }
                            });
                            finish();
                            Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                            startActivity(i);
                        } else {
                            //display some message here
                            Toast.makeText(MainActivity.this, "Wrong email or password used", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    //Register a callback function with the facebook loginButton to respond to the login result
    protected void getLoginDetails(LoginButton login_button) {
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {


                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(intent);
                AccessToken AT = AccessToken.getCurrentAccessToken();
                Log.d("access token", AT.toString());
                handleFacebookAccessToken(login_result.getAccessToken());
            }
            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
                Log.d("facebook error", exception.toString());
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();


                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume(); //logs install and app activate app event
        AppEventsLogger.activateApp(getApplication());
    }

    @Override
    protected void onPause() {
            super.onPause(); //logs app deactivate app event
    }

    //passes the activity result back to the facebook SDK
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
            Log.e("data",data.toString());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
                }
    //method to hide the keyboard after touch on listView
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}


