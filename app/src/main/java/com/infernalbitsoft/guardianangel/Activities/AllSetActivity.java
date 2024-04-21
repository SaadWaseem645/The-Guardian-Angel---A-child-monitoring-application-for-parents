package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.CompoundService;
import com.infernalbitsoft.guardianangel.Services.InstalledAppService;
import com.infernalbitsoft.guardianangel.Services.LocationService;
import com.infernalbitsoft.guardianangel.Services.MessageGrabService;

import static com.infernalbitsoft.guardianangel.Services.CompoundService.isCompoundServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.InstalledAppService.isInstalledAppServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.LocationService.isLocationServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.MessageGrabService.isMessageGrabServiceRunning;

public class AllSetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_set);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity),100).commit();

        if(!isLocationServiceRunning)
            startService(new Intent(this, LocationService.class));

        if(!isInstalledAppServiceRunning)
            startService(new Intent(this, InstalledAppService.class));

        if(!isCompoundServiceRunning)
            startService(new Intent(this, CompoundService.class));

        if(!isMessageGrabServiceRunning)
            startService(new Intent(this, MessageGrabService.class));

        Button continueButton = findViewById(R.id.allset_continue_button);
        continueButton.setOnClickListener(v -> {
            directToHomeScreen();
        });

        Button allsetBUtton = findViewById(R.id.mainactivity_button);
        allsetBUtton.setOnClickListener(v -> {
                startActivity(new Intent(this, MainMenuActivity.class));
        });

        TextView logout = findViewById(R.id.allset_logout);
        logout.setOnClickListener(v -> {
            startActivity(new Intent(this, AllSetLogoutActivity.class));
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