package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.RecyclerViews.AppListRecycler;
import com.infernalbitsoft.guardianangel.RecyclerViews.AppListRecycler.Modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.option4));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        RecyclerView recyclerView = findViewById(R.id.contacts_recycler_view);

        if (user != null)
            db.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("Contacts").get().addOnSuccessListener(dataSnapshot -> {
                ArrayList<Map<String, Object>> list = (ArrayList< Map<String, Object>>) dataSnapshot.getValue();

                if(list == null)
                    return;

                int count = 1;
                for(Map<String, Object> map : list)
                    Log(" " + count++, map.toString());

                AppListRecycler appListRecycler = new AppListRecycler(list, Modes.CONTACTS);
                recyclerView.setAdapter(appListRecycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(ContactsActivity.this, RecyclerView.VERTICAL, false));
            });
    }
}