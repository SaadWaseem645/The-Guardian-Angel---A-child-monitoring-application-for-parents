package com.infernalbitsoft.guardianangel.RecyclerViews;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infernalbitsoft.guardianangel.Activities.SocialMessageActivity;
import com.infernalbitsoft.guardianangel.R;

import java.util.ArrayList;

public class SocialConversationRecycler extends RecyclerView.Adapter<SocialConversationRecycler.viewholder> {

    private ArrayList<SocialConversationNode> conversations;

    public SocialConversationRecycler(ArrayList<SocialConversationNode> conversations) {
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public SocialConversationRecycler.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout textView = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_social_conversation, parent, false);
        return new viewholder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialConversationRecycler.viewholder holder, int position) {

        SocialConversationNode node = conversations.get(position);
        String name = node.name;

        holder.conversation.setText(name);
        if (node.isGroup)
            holder.tag.setText("Group");
        holder.view.setOnClickListener(v -> {
            Intent intent = new Intent(holder.view.getContext(), SocialMessageActivity.class);
            intent.putExtra("NAME", conversations.get(position).name);
            intent.putExtra("APP",conversations.get(position).app);
            holder.view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }


    public static class viewholder extends RecyclerView.ViewHolder {

        public View view;
        public TextView conversation;
        public TextView tag;

        viewholder(View view) {
            super(view);
            this.view = view;
            conversation = view.findViewById(R.id.social_conversation_name);
            tag = view.findViewById(R.id.social_conversation_tag);
        }

    }

    public static class SocialConversationNode {
        public String name;
        public boolean isGroup;
        public String app;
    }

}
