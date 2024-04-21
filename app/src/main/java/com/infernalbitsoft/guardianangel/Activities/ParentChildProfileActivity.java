package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;

import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class ParentChildProfileActivity extends AppCompatActivity {

    //Views
    private TextView childListFoundView;
    private ListView selectChildList;
    private ProgressBar progressBar;

    //Firebase Variables
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_profile);

        childListFoundView = findViewById(R.id.select_profile_no_list);
        selectChildList = findViewById(R.id.select_child_list);
        progressBar = findViewById(R.id.select_child_progressbar);

        TextView logout = findViewById(R.id.parent_child_profile_logout);
        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
                sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), -2).commit();
                startActivity(new Intent(ParentChildProfileActivity.this, SplashActivity.class));
                finish();
            }
        });

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), -3).commit();

        if(auth.getCurrentUser() != null) {
            db.getReference(auth.getUid()).child("ChildList").get().addOnSuccessListener(dataSnapshot -> {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    Map<String, Boolean> list = (Map<String, Boolean>) dataSnapshot.getValue();
                    String[] childList = list.keySet().toArray(new String[list.keySet().size()]);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, childList);
                    selectChildList.setAdapter(adapter);

                    selectChildList.setOnItemClickListener((parent, view, position, id) -> {
                        TextView item = (TextView) view;
                        editor.putString(getString(R.string.sharedpreferences_child_profile_name_key),item.getText().toString());
                        editor.commit();
                        Intent intent = new Intent(this, MainMenuActivity.class);
                        startActivity(intent);
                    });
                }else{
                    childListFoundView.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
                childListFoundView.setVisibility(View.VISIBLE);
            });
        }

    }
}