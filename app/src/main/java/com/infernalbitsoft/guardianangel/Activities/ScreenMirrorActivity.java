package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.MirrorRecycler;

import java.util.HashMap;
import java.util.Map;

public class ScreenMirrorActivity extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database;
    private MirrorRecycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_mirror);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.screen_mirror));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        String username = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE).getString(getString(R.string.sharedpreferences_child_profile_name_key), "");

        TextView mirrorDescription = findViewById(R.id.screen_mirror_description);
        mirrorDescription.setText(mirrorDescription.getText().toString().replace("~child~", username));
        RecyclerView recyclerView = findViewById(R.id.mirror_recycler);
        ProgressBar progressBar = findViewById(R.id.screen_mirror_progress_bar);

        database = FirebaseDatabase.getInstance().getReference().child(user.getUid())
                .child(username)
                .child("Mirror");

        Map<String, Map<String, Object>> mirrors = new HashMap<>();
        adapter = new MirrorRecycler(mirrors);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                progressBar.setVisibility(View.GONE);
                adapter.addMirror(map);
                adapter.notifyDataSetChanged();
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
    }
}