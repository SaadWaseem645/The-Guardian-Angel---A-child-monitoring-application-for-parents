package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.DataService;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealLog;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealToast;

public class MainActivity extends AppCompatActivity {


    private int accessibilityEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, DataService.class));

        TextView defaultTextView = findViewById(R.id.default_textview);

        int accessibilityEnabled = 0;
        boolean notificationEnabled = false;

        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            notificationEnabled = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(this.getPackageName());
        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }

        if(accessibilityEnabled == 1)
            defaultTextView.setText("Accessibility Enabled");
        if(notificationEnabled)
            defaultTextView.setText(defaultTextView.getText() + " \nNotifications Enabled");

//        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));

    }


}