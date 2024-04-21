package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infernalbitsoft.guardianangel.Activities.SmsActivity;
import com.infernalbitsoft.guardianangel.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class SMSRecycler extends RecyclerView.Adapter<SMSRecycler.viewholder> {

    private ArrayList<smsNode> nodes;

    public SMSRecycler(ArrayList<smsNode> sms) {
        this.nodes = sms;
    }

    @NonNull
    @Override
    public SMSRecycler.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_message, parent, false);
        return new SMSRecycler.viewholder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull SMSRecycler.viewholder holder, int position) {

        smsNode node = (smsNode) nodes.get(position);
        String message = node.message;

        Date date = new Date(node.timestamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);

        holder.message.setText(message);
        holder.timestamp.setText(formatted);

        View layout = holder.layout;
        if (!node.received) {
            layout.setBackgroundResource(R.drawable.background_message_receiver);
            RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layout.setLayoutParams(params);
        } else {
            layout.setBackgroundResource(R.drawable.background_message_sender);
            RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layout.setLayoutParams(params);
        }


    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }


    public static class viewholder extends RecyclerView.ViewHolder {

        public LinearLayout layout;
        public TextView message;
        public TextView timestamp;

        viewholder(View view) {
            super(view);
            this.layout = view.findViewById(R.id.recycler_shifting_layout);
            message = view.findViewById(R.id.recycler_layout_message_text);
            timestamp = view.findViewById(R.id.recycler_layout_date);
        }

    }

    public static class smsNode {
        public String message;
        public boolean received;
        public Long timestamp;
    }

}
