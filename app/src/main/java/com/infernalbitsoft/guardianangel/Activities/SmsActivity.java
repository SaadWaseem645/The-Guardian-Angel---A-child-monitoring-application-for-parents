package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.ConversationRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.SMSRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.SMSRecycler.smsNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class SmsActivity extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        String number = getIntent().getStringExtra("NUMBER");
        String name = getIntent().getStringExtra("NAME");

        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        String username = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE).getString(getString(R.string.sharedpreferences_child_profile_name_key), "");

        RecyclerView recyclerView = findViewById(R.id.sms_recycler);
        ProgressBar progressBar = findViewById(R.id.sms_progress_bar);
        TextView convoHeader = findViewById(R.id.conversation_header);
        convoHeader.setText(name);

        database = FirebaseDatabase.getInstance().getReference().child(user.getUid())
                .child(username)
                .child("SMSList").child(number);

        database.get().addOnSuccessListener(dataSnapshot -> {
            Map<String, Object> sms = new TreeMap<>((Map<String,Object>) dataSnapshot.getValue());
            ArrayList<smsNode> node = new ArrayList<>();

            for(String key: sms.keySet()){
                if(key.equals("name"))
                    continue;

                Map<String,Object> message = (Map<String,Object>) sms.get(key);
                smsNode tempNode = new smsNode();
                tempNode.message = (String) message.get("message");
                tempNode.received = (boolean) message.get("received");
                tempNode.timestamp = (Long) message.get("timestamp");

                node.add(tempNode);
            }

            progressBar.setVisibility(View.GONE);
            SMSRecycler adapter = new SMSRecycler(node);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            RealToast(this, "Task failed");
        });
    }
}