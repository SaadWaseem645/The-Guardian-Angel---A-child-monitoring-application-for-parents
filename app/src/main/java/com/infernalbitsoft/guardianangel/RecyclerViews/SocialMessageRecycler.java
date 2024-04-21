package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infernalbitsoft.guardianangel.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class SocialMessageRecycler extends RecyclerView.Adapter<SocialMessageRecycler.viewholder> {

    private ArrayList<msgNode> nodes;

    public SocialMessageRecycler(ArrayList<msgNode> msg) {
        this.nodes = msg;
    }

    @NonNull
    @Override
    public SocialMessageRecycler.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_message, parent, false);
        return new viewholder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        msgNode node =  nodes.get(position);

        holder.message.setText(node.message);
        holder.timestamp.setText(node.timestamp);

        View layout = holder.layout;
        if (node.sendername.isEmpty()) {
            holder.sender.setVisibility(View.GONE);
            layout.setBackgroundResource(R.drawable.background_message_receiver);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layout.setLayoutParams(params);
        } else {
            holder.sender.setVisibility(View.VISIBLE);
            holder.sender.setText(node.sendername);
            layout.setBackgroundResource(R.drawable.background_message_sender);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layout.setLayoutParams(params);
        }


    }

    public void addMessage(Map<String,Object> msg){
        msgNode node = new msgNode();
        node.message = (String) msg.get("message");
        node.isgroup = (boolean) msg.get("isgroup");
        node.datetime = (Long) msg.get("datetime");
        node.sendername = (String) msg.get("sendername");
        node.timestamp = (String) msg.get("timestamp");
        nodes.add(node);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }


    public static class viewholder extends RecyclerView.ViewHolder {

        public LinearLayout layout;
        public TextView message;
        public TextView timestamp;
        public TextView sender;

        viewholder(View view) {
            super(view);
            this.layout = view.findViewById(R.id.recycler_shifting_layout);
            message = view.findViewById(R.id.recycler_layout_message_text);
            sender = view.findViewById(R.id.recycler_layout_message_sender);
            timestamp = view.findViewById(R.id.recycler_layout_date);
        }

    }



    public static class msgNode {
        public String message;
        public String chatname;
        public String sendername;
        public boolean isgroup;
        public Long datetime;
        public String timestamp;
    }
}
