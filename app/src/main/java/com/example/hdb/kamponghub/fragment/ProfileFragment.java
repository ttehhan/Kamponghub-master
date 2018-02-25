package com.example.hdb.kamponghub.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdb.kamponghub.MainActivity;
import com.example.hdb.kamponghub.NavigationActivity;
import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.Register;
import com.example.hdb.kamponghub.models.Shop;
import com.example.hdb.kamponghub.models.User;
import com.example.hdb.kamponghub.utilities.Calculations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    //Like RegisterActivity, but update existing child, not create new child

    private FirebaseUser firebaseUser;
    private EditText email,oldPassword, newPassword, username, zipcode,phone;
    private Button updateBtn;
    private ProgressDialog progressDialog;

    private DatabaseReference userDB;
    private String uid;
    private User userDetails;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        email = (EditText) view.findViewById(R.id.r_email);
        username = (EditText) view.findViewById(R.id.r_user);
        oldPassword = (EditText) view.findViewById(R.id.r_old_password);
        newPassword = (EditText) view.findViewById(R.id.r_new_password);
        zipcode = (EditText) view.findViewById(R.id.r_postal);
        phone = (EditText) view.findViewById(R.id.r_phone);
        updateBtn = (Button) view.findViewById(R.id.update);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        userDB = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading data.");
        progressDialog.show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Shop Listing");
        updateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               updateUser();
            }
        });
        //Get data
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(User.class);
                email.setText(userDetails.getEmail(),TextView.BufferType.EDITABLE);
                username.setText(userDetails.getUsername(),TextView.BufferType.EDITABLE);
                email.setEnabled(false);
                username.setEnabled(false);
                zipcode.setText(String.valueOf(userDetails.getPostal()),TextView.BufferType.EDITABLE);
                phone.setText(String.valueOf(userDetails.getPhone()),TextView.BufferType.EDITABLE);

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w("USER", "loadShop:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getContext(), "Failed to load user details.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
        return view;
    }
    //method to update user details
    protected void updateUser() {
        try {
            String emailText = email.getText().toString().trim();
            String userNameText = username.getText().toString().trim();
            String oldPasswordText  = oldPassword.getText().toString().trim();
            final String newPasswordText= newPassword.getText().toString().trim();
            String postalText  = zipcode.getText().toString().trim();
            String phoneText  = phone.getText().toString().trim();

            if(TextUtils.isEmpty(emailText)){
                Toast.makeText(getActivity(),"Please enter email",Toast.LENGTH_LONG).show();
                return;
            }

            if(TextUtils.isEmpty(oldPasswordText)){
                Toast.makeText(getActivity(),"Please enter old password",Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(newPasswordText)){
                Toast.makeText(getActivity(),"Please enter new password",Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(postalText)){
                Toast.makeText(getActivity(),"Please enter zipcode",Toast.LENGTH_LONG).show();
                return;
            }

            if (phoneText.isEmpty()) { //phone no. is optional
                int postal = Integer.parseInt(zipcode.getText().toString().trim());
                userDetails = new User(emailText, userNameText, postal);
                //userDB.push().setValue(userDetails); //pushes the data into firebase user table
            }else {
                int phoneNo = Integer.parseInt(phone.getText().toString().trim()); //phone no. optional
                int postal = Integer.parseInt(zipcode.getText().toString().trim());
                userDetails = new User(emailText, userNameText, postal, phoneNo);
                //userDB.push().setValue(userDetails); //pushes the data into firebase user table
            }


            //if the email and password are not empty, display progress dialog
            progressDialog.setMessage("Changing profile...");
            progressDialog.show();
            //Update Firebase Auth (email and password) + "/users"
            final String email = firebaseUser.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email,oldPasswordText);
            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        firebaseUser.updatePassword(newPasswordText).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Something went wrong",Toast.LENGTH_LONG).show();
                                }else {
                                    userDB.setValue(userDetails);
                                    Toast.makeText(getActivity(), "Success",Toast.LENGTH_LONG).show();
                                    //Update Shared Preference
                                    SharedPreferences sharedPref = getContext().getSharedPreferences("USERZONE", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("userZone", userDetails.getUserZone());
                                    editor.commit();
                                    // Launch ShopDetailActivity
                                    Fragment newFragment= new ShopListingFragment();
                                    ((NavigationActivity)getActivity()).goFragment(newFragment,R.id.screen_area);
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Please enter all details correctly",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch(NullPointerException e) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Please enter all details correctly",Toast.LENGTH_LONG).show();
        }
    }

}
