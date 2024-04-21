package com.infernalbitsoft.guardianangel.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;

import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class ChildProfileActivity extends AppCompatActivity {

    //Views
    private TextView childListFoundView;
    private ListView selectChildList;
    private ProgressBar progressBar;
    private Button childSelectButton;
    private EditText childNameEditText;

    //Firebase Variables
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        childListFoundView = findViewById(R.id.select_profile_no_list);
        selectChildList = findViewById(R.id.select_child_list);
        progressBar = findViewById(R.id.select_child_progressbar);
        childSelectButton = findViewById(R.id.child_name_submit);
        childNameEditText = findViewById(R.id.child_select_name);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 10).commit();
        SharedPreferences.Editor editor = sharedpreferences.edit();

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
                        Intent intent = new Intent(this, SetPinActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }else{
                    childListFoundView.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                childListFoundView.setVisibility(View.VISIBLE);
            });
        }

        childSelectButton.setOnClickListener(v -> {
               String name = childNameEditText.getText().toString().trim();
               if(name.isEmpty())
                   RealToast(this, "Please enter a name");
               else{
                   db.getReference(auth.getUid()).child("ChildList").child(name).setValue(true).addOnSuccessListener(aVoid -> {
                       editor.putString(getString(R.string.sharedpreferences_child_profile_name_key),name);
                       editor.commit();
                       Intent intent = new Intent(this, SetPinActivity.class);
                       startActivity(intent);
                       finish();
                   }).addOnFailureListener(e -> {
                        RealToast(this, "Failed to create new user");
                   });
               }
        });

    }
}