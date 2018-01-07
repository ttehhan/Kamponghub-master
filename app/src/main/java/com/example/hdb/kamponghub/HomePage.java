package com.example.hdb.kamponghub;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;


public class HomePage extends AppCompatActivity {
    //private ImageButton chatBtn;
   private String UserEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
      //  chatBtn = (ImageButton)(findViewById(R.id.chatButton));
        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {UserEmail = null;}
        else
        {UserEmail = extras.getString("email");}

       /* chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();

                Intent i = new Intent(HomePage.this,Chat.class);
                i.putExtra("email", UserEmail);
                startActivity(i);
            }
        });*/

    }
    public void logOut() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest (AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                AccessToken.setCurrentAccessToken(null);

            }
        }).executeAsync();
    }
}
