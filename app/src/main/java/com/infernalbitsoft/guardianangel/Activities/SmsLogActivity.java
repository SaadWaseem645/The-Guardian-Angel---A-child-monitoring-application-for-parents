package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.ConversationRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.ConversationRecycler.conversationNode;
import com.infernalbitsoft.guardianangel.RecyclerViews.MirrorRecycler;

import java.util.ArrayList;
import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class SmsLogActivity extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database;
    private ConversationRecycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_log);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.option1));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        String username = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE).getString(getString(R.string.sharedpreferences_child_profile_name_key), "");

        RecyclerView recyclerView = findViewById(R.id.conversation_recycler);
        ProgressBar progressBar = findViewById(R.id.conversation_progress_bar);

        database = FirebaseDatabase.getInstance().getReference().child(user.getUid())
                .child(username)
                .child("SMSList");

        database.get().addOnSuccessListener(dataSnapshot -> {
            Map<String, Object> conversations = (Map<String,Object>) dataSnapshot.getValue();
            ArrayList<conversationNode> node = new ArrayList<>();
            progressBar.setVisibility(View.GONE);

            if(conversations == null)
                return;

            for(String key: conversations.keySet()){
                Map<String,Object> conversation = (Map<String,Object>) conversations.get(key);

                conversationNode convo = new conversationNode();
                convo.name = (String) conversation.get("name");
                convo.number = key;
                node.add(convo);
            }
            adapter = new ConversationRecycler(node);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            RealToast(this, "Task failed");
        });
    }
}