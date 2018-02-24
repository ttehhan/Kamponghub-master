package com.example.hdb.kamponghub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hdb.kamponghub.fragment.BookMarkFragment;
import com.example.hdb.kamponghub.fragment.ChatFragment;
import com.example.hdb.kamponghub.models.MyApplication;
import com.example.hdb.kamponghub.fragment.MapsFragment;
import com.example.hdb.kamponghub.fragment.ProfileFragment;
import com.example.hdb.kamponghub.fragment.ShopListingFragment;
import com.example.hdb.kamponghub.models.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavigationActivity extends AppCompatActivity{

    //private TextView mTextMessage;
   // private ImageButton chatBtn;
   private Fragment fragment;
    //Firebase variable
    private FirebaseAuth mAuth=FirebaseAuth.getInstance(); // <== To prevent null error in getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //chatBtn = (ImageButton)(findViewById(R.id.chatButton));
       // mTextMessage = (TextView)findViewById(R.id.message);

       /* chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();

                Intent i = new Intent(NavigationActivity.this,Chat.class);
                i.putExtra("email", UserEmail);
                startActivity(i);
            }
        });*/

        //Populate Content Area with Fragment
        fragment = new ShopListingFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.screen_area,fragment);
        fragmentTransaction.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    public void logOut() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                AccessToken.setCurrentAccessToken(null);

            }
        }).executeAsync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
        //noinspection SimplifiableIfStatement

            case R.id.menu_filter:
                Toast.makeText(this,"Filter",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_search:
                Toast.makeText(this,"Search",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.navigation_profile:
                // mTextMessage.setText(R.string.title_profile);
                Toast.makeText(NavigationActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                fragment= new ProfileFragment();
                goFragment(fragment,R.id.screen_area);
                return true;
            case R.id.menu_logout:
                logOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            case R.id.menu_maps:
                fragment= new MapsFragment();
                goFragment(fragment,R.id.screen_area);
                return true;
            //return super.onOptionsItemSelected(item);
        }
        return false;
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {

                case R.id.navigation_store:
                    //mTextMessage.setText(R.string.title_chat);
                    //Toast.makeText(NavigationActivity.this, "Store", Toast.LENGTH_SHORT).show();
                    fragment= new ShopListingFragment();
                    goFragment(fragment,R.id.screen_area);
                    return true;

                case R.id.navigation_chat:
                    //mTextMessage.setText(R.string.title_chat);
                    //Toast.makeText(NavigationActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                    fragment= new ChatFragment();
                    goFragment(fragment,R.id.screen_area);
                    //Intent i = new Intent(NavigationActivity.this,Chat.class);
                    //startActivity(i);
                    return true;
                case R.id.navigation_bookmark:
                   // mTextMessage.setText(R.string.title_bookmark);
                    // Toast.makeText(NavigationActivity.this, "Bookmark", Toast.LENGTH_SHORT).show();
                   fragment = new BookMarkFragment();
                    goFragment(fragment,R.id.screen_area);
                    return true;

            }
            return false;
        }
    };
    public void goFragment(Fragment fragment, int toReplace){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //For replace: refers to the FrameLayout in "content_main"
        ft.replace(toReplace,fragment)
                .addToBackStack(null)
                .commit();
    }

    //Get Firebase Uid
    public String getUid() {
        return mAuth.getCurrentUser().getUid();
    }

}
