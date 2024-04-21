package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.SocialMessageRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.SocialMessageRecycler.msgNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class SocialMessageActivity extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database;
    SocialMessageRecycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_message);

        String name = getIntent().getStringExtra("NAME");
        String app = getIntent().getStringExtra("APP");

        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        String username = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE).getString(getString(R.string.sharedpreferences_child_profile_name_key), "");

        RecyclerView recyclerView = findViewById(R.id.message_recycler);
        ProgressBar progressBar = findViewById(R.id.message_progress_bar);
        TextView convoHeader = findViewById(R.id.conversation_header);
        convoHeader.setText(name);

        database = FirebaseDatabase.getInstance().getReference().child(user.getUid())
                .child(username)
                .child("Messages").child(app).child(name);

//        database.get().addOnSuccessListener(dataSnapshot -> {
//            Map<String, Object> message = new TreeMap<>((Map<String,Object>) dataSnapshot.getValue());
            ArrayList<msgNode> node = new ArrayList<>();

//            for(String key: message.keySet()){
//                if(key.equals("isgroup"))
//                    continue;

//                Map<String,Object> msg = (Map<String,Object>) message.get(key);
//                msgNode tempNode = new msgNode();
//                tempNode.message = (String) msg.get("message");
//                tempNode.isgroup = (boolean) msg.get("isgroup");
//                tempNode.datetime = (Long) msg.get("datetime");
//                tempNode.sendername = (String) msg.get("sendername");
//                tempNode.timestamp = (String) msg.get("timestamp");


//                Log.d("Messages", "OK"+tempNode.message + tempNode.isgroup + tempNode.datetime + tempNode.sendername +tempNode.timestamp);

//                node.add(tempNode);
//            }

            adapter = new SocialMessageRecycler(node);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            database.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Object node = snapshot.getValue();
                    if(node instanceof Map<?, ?>)
                        adapter.addMessage((Map<String,Object>) node);

                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//        }).addOnFailureListener(e -> {
//            RealToast(this, "Task failed");
//        });

    }
}