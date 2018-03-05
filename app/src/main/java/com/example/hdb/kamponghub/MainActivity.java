package com.example.hdb.kamponghub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.FaceDetector;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity; //make sures that it is backward compatible
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdb.kamponghub.models.MyApplication;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FacebookAuthProvider;

import com.example.hdb.kamponghub.models.User;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import org.json.JSONObject;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private EditText emailText, passwordText;
    private Button loginBtn, registerBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String FBEmail, email, password;
    private MyApplication myApp;
    private DatabaseReference userDB;
    //Have another reference for logged in user
    private DatabaseReference loginUser;
    private TextView forgetPass;
    private LoginButton FBloginBtn;
    private String userZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApp = (MyApplication) getApplicationContext();
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login);
        forgetPass = findViewById(R.id.forgot_password);
        callbackManager = CallbackManager.Factory.create();
        FBloginBtn = findViewById(R.id.fb_login);
        FBloginBtn.setReadPermissions("email", "public_profile");
        getLoginDetails(FBloginBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        progressDialog = new ProgressDialog(this);
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

        //TODO add in workflow for send password to email
        forgetPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String emailAddress = "tester1@test.com";
                Toast.makeText(MainActivity.this,"Password recovery mail sent",Toast.LENGTH_LONG).show();
                /*auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                }
                            }
                        });*/
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
            email = "tester2@test.com";
            password ="123456";
        }
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
                                loginUser = FirebaseDatabase.getInstance().getReference().child("users").child(userUid);
                                loginUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        myApp.setUserName(user.getUsername());
                                        SharedPreferences sharedPref = getSharedPreferences("USERZONE", Context.MODE_PRIVATE);
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
                AccessToken AT = AccessToken.getCurrentAccessToken();
                Log.d("access token", AT.toString());
                handleFacebookAccessToken(login_result.getAccessToken());
                GraphRequest.newMeRequest(
                        login_result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                try {
                                    if (response.getError() != null) {
                                        // handle error
                                    } else {
                                        // get id of user
                                        FBEmail = me.getString("id")+"@facebook.com";
                                        myApp.setEmail(FBEmail);
                                    }
                                } catch (JSONException e) {
                                }
                            }
                        }).executeAsync();

                //TODO how to check the email exists in the user table only for first time users then go FB Register
                if(!checkIfEmailExistInFirebase(FBEmail)) {
                    Intent i = new Intent(MainActivity.this, FacebookRegisterActivity.class);
                    startActivity(i);

                }else {
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    startActivity(i);
                }
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
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Facebook authentication failed.",
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

    //method to check if FB email is not the first time login
    public boolean checkIfEmailExistInFirebase(String emailAddress)
    {
        final String emailAdd = emailAddress;
        final boolean[] emailExist = new boolean[1];
        Query getUser = userDB.orderByChild("email").equalTo(emailAdd);
        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()) {
                        emailExist[0] = true;
                    }else
                    { emailExist[0] = false;}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return emailExist[0];
    }



}


