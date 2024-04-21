package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infernalbitsoft.guardianangel.R;

public class MirrorFullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror_full);

        String url = getIntent().getExtras().getString("url");
        String timestamp = getIntent().getExtras().getString("timestamp");

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(timestamp);
        headerTitle.setTextSize(12);

        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        ImageView imageView = findViewById(R.id.mirror_full_image);

        Glide.with(this).load(url).into(imageView);
    }
}