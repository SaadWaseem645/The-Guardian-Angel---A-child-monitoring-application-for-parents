package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.AppListRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.AppListRecycler.Modes;
import com.infernalbitsoft.guardianangel.Services.InstalledAppService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class InstalledAppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_apps);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.option5));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        ProgressBar bar =  findViewById(R.id.installed_app_progress_bar);

        RecyclerView recyclerView = findViewById(R.id.installed_apps_recycler_view);

        if (user != null)
            db.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("AppList").get().addOnSuccessListener(dataSnapshot -> {
                Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                bar.setVisibility(View.GONE);

                if(map == null)
                    return;

                AppListRecycler appListRecycler = new AppListRecycler(map, Modes.APP_LIST);
                recyclerView.setAdapter(appListRecycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(InstalledAppsActivity.this, RecyclerView.VERTICAL, false));
                recyclerView.setVisibility(View.VISIBLE);
            });
    }
}