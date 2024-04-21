package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infernalbitsoft.guardianangel.Model.AppLimitClass;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.CompoundService;
import com.infernalbitsoft.guardianangel.Services.DataService;
import com.infernalbitsoft.guardianangel.Services.InstalledAppService;
import com.infernalbitsoft.guardianangel.Services.LocationService;
import com.infernalbitsoft.guardianangel.Services.MessageGrabService;

import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.xtooltip.ClosePolicy;
import it.sephiroth.android.library.xtooltip.Tooltip;

import static android.view.View.GONE;
import static com.infernalbitsoft.guardianangel.Services.CompoundService.isCompoundServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.InstalledAppService.isInstalledAppServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.LocationService.isLocationServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.MessageGrabService.isMessageGrabServiceRunning;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class MainMenuActivity extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database;
    private ValueEventListener valueEventListener;

    private String[] languages = {"English", "Urdu"};
    private LinearLayout languageChange;

    private MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), -1).commit();

        languageChange = findViewById(R.id.login_language_change);
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setItems(languages, (dialog, which) -> {
            changeLanguage(languages[which]);
        });

        languageChange.setOnClickListener(v -> {
            build.show();
        });

        database = FirebaseDatabase.getInstance().getReference().child(user.getUid())
                .child(sharedpreferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("status").child("state");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String state = snapshot.getValue(String.class);

                TextView onlineStatus = findViewById(R.id.main_menu_online_status_text);
                ImageView onlineStatusImage = findViewById(R.id.main_menu_online_status_image);

                if (state == null)
                    return;

                if (state.equals("online")) {
                    onlineStatus.setText(R.string.online);
                    onlineStatusImage.setImageDrawable(ContextCompat.getDrawable(MainMenuActivity.this, R.drawable.ic_online));
                } else {
                    onlineStatus.setText(R.string.offline);
                    onlineStatusImage.setImageDrawable(ContextCompat.getDrawable(MainMenuActivity.this, R.drawable.ic_offline));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.addValueEventListener(valueEventListener);

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                    FirebaseDatabase.getInstance().getReference().child(user.getUid())
                            .child(sharedpreferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                            .child("KillCommand").setValue(true).addOnSuccessListener(aVoid -> {
                        FirebaseAuth.getInstance().signOut();
                    });
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    FirebaseAuth.getInstance().signOut();
                    break;
            }
        };

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(this, SplashActivity.class));
                finish();
            }
        };

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authStateListener);

//        Log("IsCompoundRunning", isCompoundServiceRunning + " ");

//        isMyServiceRunning();

        LinearLayout socialMediaButton = findViewById(R.id.social_media_button);
        socialMediaButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SocialMediaActivity.class));
        });

        LinearLayout logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stop Monitoring before logging out?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        LinearLayout smslog = findViewById(R.id.option_converations);
        smslog.setOnClickListener(v -> {
            startActivity(new Intent(this, SmsLogActivity.class));
        });

        LinearLayout installedApplications = findViewById(R.id.installed_application_list);
        installedApplications.setOnClickListener(v -> {
            startActivity(new Intent(this, InstalledAppsActivity.class));
        });

        LinearLayout appUsage = findViewById(R.id.app_usage_list);
        appUsage.setOnClickListener(v -> {
            startActivity(new Intent(this, AppUsageActivity.class));
        });

        LinearLayout location = findViewById(R.id.option_location);
        location.setOnClickListener(v -> {
            startActivity(new Intent(this, LocationActivity.class));
        });

        LinearLayout contacts = findViewById(R.id.option_contacts);
        contacts.setOnClickListener(v -> {
            startActivity(new Intent(this, ContactsActivity.class));
        });

        LinearLayout geotrigger = findViewById(R.id.option_geotrigger);
        geotrigger.setOnClickListener(v -> {
            startActivity(new Intent(this, GeofenceHistoryActivity.class));
        });

        LinearLayout appSchedule = findViewById(R.id.option_app_schedule);
        appSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, AppLimitsActivity.class));
        });

        LinearLayout changeProfileButton = findViewById(R.id.main_menu_profile_change_button);
        changeProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ParentChildProfileActivity.class));
        });

        LinearLayout screenMirrorButton = findViewById(R.id.main_menu_screen_mirror_button);
        screenMirrorButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ScreenMirrorActivity.class));
        });

        LinearLayout changePinButton = findViewById(R.id.changePinOption);
        changePinButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ParentSetPinActivity.class));
        });

        ImageView logoutInfo = findViewById(R.id.tooltip_logout);
        ImageView mirrorInfo = findViewById(R.id.tooltip_mirror);
        ImageView appsInfo = findViewById(R.id.tooltip_apps);
        ImageView smsInfo = findViewById(R.id.tooltip_sms);
        ImageView socialInfo = findViewById(R.id.tooltip_social);
        ImageView contactsInfo = findViewById(R.id.tooltip_contacts);
        ImageView screentimeInfo = findViewById(R.id.tooltip_screentime);
        ImageView locationInfo = findViewById(R.id.tooltip_location);
        ImageView triggerInfo = findViewById(R.id.tooltip_trigger);
        ImageView scheduleInfo = findViewById(R.id.tooltip_schedule);
        ImageView pinInfo = findViewById(R.id.tooltip_change_pin);
        ImageView stopInfo = findViewById(R.id.tooltip_stop);


        setTooltips(logoutInfo, R.string.tooltip_logout);
        setTooltips(mirrorInfo, R.string.tooltip_mirror);
        setTooltips(appsInfo, R.string.tooltip_apps);
        setTooltips(smsInfo, R.string.tooltip_sms);
        setTooltips(socialInfo, R.string.tooltip_social);
        setTooltips(contactsInfo, R.string.tooltip_contacts);
        setTooltips(screentimeInfo, R.string.tooltip_screentime);
        setTooltips(locationInfo, R.string.tooltip_location);
        setTooltips(triggerInfo, R.string.tooltip_trigger);
        setTooltips(scheduleInfo, R.string.tooltip_schedule);
        setTooltips(pinInfo, R.string.tooltip_change_pin);
        setTooltips(stopInfo, R.string.tooltip_stop);

        LinearLayout stopMonitoring = findViewById(R.id.stopMonitoringOption);
        stopMonitoring.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child(user.getUid())
                    .child(sharedpreferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("KillCommand").setValue(true);
        });

        TextView profileName = findViewById(R.id.profile_name);
        profileName.setText(sharedpreferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""));

    }

    private void setTooltips(ImageView view, int stringId) {

        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        String lang = preferences.getString(getString(R.string.sharedpreferenced_language), "");

        view.setOnClickListener(v -> {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(stringId), Snackbar.LENGTH_LONG);
            snackbar.setDuration(4000).setBackgroundTint(getColor(R.color.black)).setTextColor(getColor(R.color.white)).show();

            if (mp != null && mp.isPlaying())
                mp.stop();

            if (lang.equals("en") || lang.equals("")) {
                switch (stringId) {
                    case R.string.tooltip_logout:
                        mp = MediaPlayer.create(this, R.raw.logout_e);
                        break;
                    case R.string.tooltip_mirror:
                        mp = MediaPlayer.create(this, R.raw.mirror_e);
                        break;
                    case R.string.tooltip_apps:
                        mp = MediaPlayer.create(this, R.raw.apps_e);
                        break;
                    case R.string.tooltip_sms:
                        mp = MediaPlayer.create(this, R.raw.sms_e);
                        break;
                    case R.string.tooltip_social:
                        mp = MediaPlayer.create(this, R.raw.social_e);
                        break;
                    case R.string.tooltip_contacts:
                        mp = MediaPlayer.create(this, R.raw.contacts_e);
                        break;
                    case R.string.tooltip_screentime:
                        mp = MediaPlayer.create(this, R.raw.screentime_e);
                        break;
                    case R.string.tooltip_location:
                        mp = MediaPlayer.create(this, R.raw.location_e);
                        break;
                    case R.string.tooltip_trigger:
                        mp = MediaPlayer.create(this, R.raw.trigger_e);
                        break;
                    case R.string.tooltip_schedule:
                        mp = MediaPlayer.create(this, R.raw.schedule_e);
                        break;
                    case R.string.tooltip_change_pin:
                        mp = MediaPlayer.create(this, R.raw.change_pin_e);
                        break;
                    case R.string.tooltip_stop:
                        mp = MediaPlayer.create(this, R.raw.stop_e);
                        break;
                }

                mp.start();
            }
            else {
                switch (stringId) {
                    case R.string.tooltip_logout:
                        mp = MediaPlayer.create(this, R.raw.logout_u);
                        break;
                    case R.string.tooltip_mirror:
                        mp = MediaPlayer.create(this, R.raw.mirror_u);
                        break;
                    case R.string.tooltip_apps:
                        mp = MediaPlayer.create(this, R.raw.apps_u);
                        break;
                    case R.string.tooltip_sms:
                        mp = MediaPlayer.create(this, R.raw.sms_u);
                        break;
                    case R.string.tooltip_social:
                        mp = MediaPlayer.create(this, R.raw.social_u);
                        break;
                    case R.string.tooltip_contacts:
                        mp = MediaPlayer.create(this, R.raw.contacts_u);
                        break;
                    case R.string.tooltip_screentime:
                        mp = MediaPlayer.create(this, R.raw.screentime_u);
                        break;
                    case R.string.tooltip_location:
                        mp = MediaPlayer.create(this, R.raw.location_u);
                        break;
                    case R.string.tooltip_trigger:
                        mp = MediaPlayer.create(this, R.raw.trigger_u);
                        break;
                    case R.string.tooltip_schedule:
                        mp = MediaPlayer.create(this, R.raw.schedule_u);
                        break;
                    case R.string.tooltip_change_pin:
                        mp = MediaPlayer.create(this, R.raw.change_pin_u);
                        break;
                    case R.string.tooltip_stop:
                        mp = MediaPlayer.create(this, R.raw.stop_u);
                        break;
                }

                mp.start();
            }


//            if (stringId == R.string.tooltip_apps) {
//                mp = MediaPlayer.create(this, R.raw.sound_1);
//                mp.start();
//            } else if (stringId == R.string.tooltip_contacts) {
//                mp = MediaPlayer.create(this, R.raw.sound_2);
//                mp.start();
//            }

        });

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int width = displayMetrics.widthPixels;
//
//        view.setOnClickListener((View v) -> new Tooltip.Builder(v.getContext())
//                .anchor(v, 0, 0, true)
//                .text(getString(stringId))
//                .arrow(true)
//                .styleId(R.style.CustomTooltip)
//                .showDuration(4000)
//                .maxWidth((int) width / 2)
//                .overlay(false)
//                .closePolicy(ClosePolicy.Companion.getTOUCH_ANYWHERE_CONSUME())
//                .create().show(v, Tooltip.Gravity.BOTTOM, true)
//        );
    }

    private void checkLanguage() {
        Locale current = getResources().getConfiguration().getLocales().get(0);

        if ("ur".equals(current.getLanguage())) {
            findViewById(R.id.online_status).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    private void changeLanguage(String lang) {

        Locale locale;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        if (lang.equals("Urdu")) {
            locale = new Locale("ur");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            preferences.edit().putString(getString(R.string.sharedpreferenced_language), "ur").commit();

        } else {
            locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            preferences.edit().putString(getString(R.string.sharedpreferenced_language), "en").commit();

        }
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null && mp.isPlaying())
            mp.stop();
    }

    private void isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log("RunningServices", "This " + service.service.getClassName());
        }
    }
}