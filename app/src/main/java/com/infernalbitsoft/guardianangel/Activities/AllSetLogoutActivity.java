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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.CompoundService;
import com.infernalbitsoft.guardianangel.Services.InstalledAppService;
import com.infernalbitsoft.guardianangel.Services.LocationService;
import com.infernalbitsoft.guardianangel.Services.MessageGrabService;
import com.infernalbitsoft.guardianangel.Services.NotificationListener;
import com.infernalbitsoft.guardianangel.Services.ScreenCaptureService;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class AllSetLogoutActivity extends AppCompatActivity {

    //Views
    private PinView pinView;
    private Button setPinContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_set_logout);

        pinView = findViewById(R.id.get_pin_view);
        setPinContinue = findViewById(R.id.get_pin_continue);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        String pin = sharedpreferences.getString(getString(R.string.sharedpreferences_set_pin_key), "33");

        pinView.setShowSoftInputOnFocus(false);
        setPinContinue.setOnClickListener(v -> {
            if(pinView.getText().toString().length() < 4) {
                Toast(setPinContinue.getContext(), "Please enter complete pin code");
            }else if(!pinView.getText().toString().equals(pin)){
                Toast(setPinContinue.getContext(), "Invalid pin code");
            }else if(pinView.getText().toString().equals(pin)){
                stopService(new Intent(this, InstalledAppService.class));
                stopService(new Intent(this, LocationService.class));
                stopService(new Intent(this, MessageGrabService.class));
                stopService(new Intent(this, NotificationListener.class));
                stopService(new Intent(this, ScreenCaptureService.class));
                stopService(new Intent(this, CompoundService.class));
                FirebaseAuth.getInstance().signOut();
                sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), -2).commit();
                startActivity(new Intent(this, SplashActivity.class));
                finish();
            }
        });
    }

    public void addText(View view) {
        pinView.setText(pinView.getText().toString() + ((TextView) view).getText().toString());
    }

    public void removeText(View view){
        pinView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }
}