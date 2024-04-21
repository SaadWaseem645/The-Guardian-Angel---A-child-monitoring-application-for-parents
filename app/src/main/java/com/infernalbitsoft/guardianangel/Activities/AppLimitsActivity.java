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
import com.infernalbitsoft.guardianangel.RecyclerViews.AppLimitRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.GeofenceHistoryRecycler;

import java.util.Map;

public class AppLimitsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_limits);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.appScreenTimeLimit));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        ProgressBar bar =  findViewById(R.id.app_limit_progress_bar);

        RecyclerView recyclerView = findViewById(R.id.app_limit_recycler_view);

        if (user != null)
            db.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("AppUsageLimit").get().addOnSuccessListener(dataSnapshot -> {
                Map<String, Long> map = (Map<String, Long>) dataSnapshot.getValue();

                bar.setVisibility(View.GONE);

                if(map == null)
                    return;

                AppLimitRecycler appLimitRecycler = new AppLimitRecycler(map);
                recyclerView.setAdapter(appLimitRecycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(AppLimitsActivity.this, RecyclerView.VERTICAL, false));
                recyclerView.setVisibility(View.VISIBLE);
            });
    }
}