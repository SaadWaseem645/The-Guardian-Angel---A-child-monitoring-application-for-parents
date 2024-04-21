package com.infernalbitsoft.guardianangel.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.Activities.SocialMediaActivity;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.SocialConversationRecycler;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class MessengerFragment extends Fragment {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database;
    private SocialConversationRecycler adapter;

    public MessengerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger, container, false);
        String username = view.getContext().getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE).getString(getString(R.string.sharedpreferences_child_profile_name_key), "");

        RecyclerView recyclerView = view.findViewById(R.id.messenger_conversation_recycler);

        database = FirebaseDatabase.getInstance().getReference().child(user.getUid())
                .child(username)
                .child("Messages").child("Messenger");

        database.get().addOnSuccessListener(dataSnapshot -> {
            Map<String, Object> conversations = (Map<String, Object>) dataSnapshot.getValue();
            ArrayList<SocialConversationRecycler.SocialConversationNode> node = new ArrayList<>();

            if (conversations != null)
                for (String key : conversations.keySet()) {
                    Map<String, Object> conversation = (Map<String, Object>) conversations.get(key);

                    SocialConversationRecycler.SocialConversationNode convo = new SocialConversationRecycler.SocialConversationNode();
                    convo.isGroup = (boolean) conversation.get("isgroup");
                    convo.name = key;
                    convo.app = "Messenger";
                    node.add(convo);
                }
            adapter = new SocialConversationRecycler(node);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            RealToast(view.getContext(), "Task failed");
        });
        return view;

    }

}