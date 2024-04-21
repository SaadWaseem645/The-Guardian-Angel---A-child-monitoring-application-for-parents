package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infernalbitsoft.guardianangel.Activities.SmsActivity;
import com.infernalbitsoft.guardianangel.R;

import java.util.ArrayList;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class ConversationRecycler extends RecyclerView.Adapter<ConversationRecycler.viewholder> {

    private ArrayList<conversationNode> conversations;

    public ConversationRecycler(ArrayList<conversationNode> conversations){
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ConversationRecycler.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_conversation,parent,false);
        return new ConversationRecycler.viewholder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationRecycler.viewholder holder, int position) {

        conversationNode node = conversations.get(position);
        String name = node.name;


        if(name.equals(""))
            name = node.number;

        holder.conversation.setText(name);
        holder.view.setOnClickListener(v -> {
            Intent intent = new Intent(holder.view.getContext(), SmsActivity.class);
            intent.putExtra("NUMBER", conversations.get(position).number);
            intent.putExtra("NAME", holder.conversation.getText().toString());
            holder.view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }


    public static class viewholder extends RecyclerView.ViewHolder{

        public View view;
        public TextView conversation;

        viewholder(View view){
            super(view);
            this.view = view;
            conversation = view.findViewById(R.id.conversation);
        }

    }

    public static class conversationNode{
        public String name;
        public String number;
    }

}
