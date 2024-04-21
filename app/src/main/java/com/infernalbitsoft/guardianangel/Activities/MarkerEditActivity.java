package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.infernalbitsoft.guardianangel.R;

public class MarkerEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_edit);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.editGeofence));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());


    }
}