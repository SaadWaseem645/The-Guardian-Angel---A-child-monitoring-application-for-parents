package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.infernalbitsoft.guardianangel.R;

public class AppRestrictedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_restricted);

        Button continueButton = findViewById(R.id.restricted_continue_button);
        continueButton.setOnClickListener(v -> {
            directToHomeScreen();
        });
    }

    @Override
    public void onBackPressed() {
        directToHomeScreen();
    }

    private void directToHomeScreen(){
        Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startHomescreen);
        finish();
    }
}