package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
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
import com.infernalbitsoft.guardianangel.RecyclerViews.AppListRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.GeofenceHistoryRecycler;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class GeofenceHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_history);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.geofence_trigger));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        ProgressBar bar =  findViewById(R.id.geofence_history_progress_bar);

        RecyclerView recyclerView = findViewById(R.id.geofence_history_recycler_view);

        if (user != null)
            db.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("GeofenceHistory").get().addOnSuccessListener(dataSnapshot -> {
                Map<String, Map<String, Object>> unsortedTriggers = (Map<String, Map<String, Object>>) dataSnapshot.getValue();

                bar.setVisibility(View.GONE);
                if(unsortedTriggers == null)
                    return;

                Map<String, Map<String, Object>> map = new TreeMap<>(Collections.reverseOrder());


                map.putAll(unsortedTriggers);


                GeofenceHistoryRecycler geofenceHistoryRecycler = new GeofenceHistoryRecycler(map);
                recyclerView.setAdapter(geofenceHistoryRecycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(GeofenceHistoryActivity.this, RecyclerView.VERTICAL, false));
                recyclerView.setVisibility(View.VISIBLE);
            });
    }
}