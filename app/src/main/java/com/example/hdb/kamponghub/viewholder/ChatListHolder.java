package com.example.hdb.kamponghub.viewholder;

/**
 * Created by TTH on 25/02/2018.
 */
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.example.hdb.kamponghub.R;
import com.example.hdb.kamponghub.models.ChatMessage;
import com.example.hdb.kamponghub.utilities.Calculations;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

public class ChatListHolder extends RecyclerView.ViewHolder{
    private static final String TAG = ChatListHolder.class.getSimpleName();
    public ImageView shopProfile;
    public TextView shopName;
    public TextView chatDateTime;
    public TextView msgContents;



    public ChatListHolder(View itemView) {
        super(itemView);
        //shopProfile = (CircleImageView) itemView.findViewById(R.id.list_shop_profile);
        shopName = (TextView)itemView.findViewById(R.id.shopName);
        chatDateTime = (TextView)itemView.findViewById(R.id.isShopOpen);
        msgContents = (TextView)itemView.findViewById(R.id.image_message_body);

    }
    public void bindToList(ChatMessage chatMsg, View.OnClickListener clickListener) {
        //setProfile(chatMsg.getImage());
        setShopName(chatMsg.getSender());
        setDateTime(chatMsg.getDate(), chatMsg.getTime());
        setMsgContent(chatMsg.getMsg()); //gets the latest chat msg
    }


    /*private void setProfile(String shopImageString)
    {
        //Get Bitmap from base64
        Bitmap bitmap = Calculations.base64ToBitmap(shopImageString);
        shopProfile.setImageBitmap(bitmap);
    }*/

    private void setShopName(String title)
    {
        shopName.setText(title);
    }

    public void setDateTime(String date, String time){

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MM yyyy hh:mm a");
        String dateTime = date.concat(time);
        String strTime = sdf.format(dateTime);
        chatDateTime.setText(strTime);
    }

    public void setMsgContent(String msg){
        //TODO: Detect that if it is an image, show something else
        msgContents.setText(msg);
    }
}
