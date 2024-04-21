package com.infernalbitsoft.guardianangel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.infernalbitsoft.guardianangel.R;

import static com.infernalbitsoft.guardianangel.Services.CompoundService.lastApp;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class AppLockActivity extends AppCompatActivity {

    //Views
    private PinView pinView;
    private Button setPinContinue;
    private Button setPinBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        String appName = getIntent().getStringExtra("APP_NAME");

        pinView = findViewById(R.id.get_pin_view);
        setPinContinue = findViewById(R.id.get_pin_continue);
        setPinBack = findViewById(R.id.get_pin_back);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        String pin = sharedpreferences.getString(getString(R.string.sharedpreferences_set_pin_key), "33");

        pinView.setShowSoftInputOnFocus(false);
        setPinContinue.setOnClickListener(v -> {
            if(pinView.getText().toString().length() < 4) {
                Toast(setPinContinue.getContext(), "Please enter complete pin code");
            }else if(!pinView.getText().toString().equals(pin)){
                Toast(setPinContinue.getContext(), "Invalid pin code");
            }else if(pinView.getText().toString().equals(pin)){
                lastApp = appName;
                finish();
            }
        });

        setPinBack.setOnClickListener(v -> {
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

    public void addText(View view) {
        pinView.setText(pinView.getText().toString() + ((TextView) view).getText().toString());
    }

    public void removeText(View view){
        pinView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }
}