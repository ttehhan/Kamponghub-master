package com.example.hdb.kamponghub.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hdb.kamponghub.models.ChatMessage;
import com.example.hdb.kamponghub.R;

import java.util.List;
/**
 * Created by TTH on 25/12/2017.
 */

public class MessageAdapter extends ArrayAdapter<ChatMessage> {
    private Activity activity;
    private List<ChatMessage> messages;
    private ChatMessage chatMessage;

    public MessageAdapter(Activity context, int resource, List<ChatMessage> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0; // determined by view type
        chatMessage = getItem(position);
        int viewType = getItemViewType(position);

        if (chatMessage.getMsgType()) { //if true, will put as my messsages
            layoutResource = R.layout.message_sent;
        }
        else {
            layoutResource = R.layout.received_message;
        }

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //set message content
        holder.msg.setText(chatMessage.getMsg());
        holder.time.setText(chatMessage.getDate()+", "+ chatMessage.getTime());
        if(!chatMessage.getMsgType())
        {holder.name.setText(chatMessage.getSender());}

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        if(messages.get(position).getMsgType() == true)
        {return 0;}
        else
        {return 1;}
    }

    private class ViewHolder {
        private TextView name;
        private TextView msg;
        private TextView time;


        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.text_message_name);
            msg = (TextView) v.findViewById(R.id.text_message_body);
            time = (TextView) v.findViewById(R.id.text_message_time);

        }
    }

}
