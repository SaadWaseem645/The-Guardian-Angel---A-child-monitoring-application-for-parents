package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.card.MaterialCardView;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.MessageGrabService;

import java.util.Locale;

import static android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.infernalbitsoft.guardianangel.Utilities.GlobalVariables.SCREEN_MIRROR_PERMISSION_CODE;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class PermissionsActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    private MaterialCardView locationPermission;
    private MaterialCardView notificationPermission;
    private MaterialCardView accessibilityPermission;
    private MaterialCardView contactsPermission;
    private MaterialCardView mirrorPermission;
    private MaterialCardView popupPermission;
    private MaterialCardView smsPermission;
    private MaterialCardView usagePermission;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        locationPermission = findViewById(R.id.permission_location);
        notificationPermission = findViewById(R.id.permission_notification);
        accessibilityPermission = findViewById(R.id.permission_accessibility);
        contactsPermission = findViewById(R.id.permission_contacts);
        mirrorPermission = findViewById(R.id.permission_mirror);
        popupPermission = findViewById(R.id.permission_popup);
        smsPermission = findViewById(R.id.permission_sms);
        usagePermission = findViewById(R.id.permission_usage);
        continueButton = findViewById(R.id.permission_button_continue);

        locationPermission.setOnClickListener(this);
        notificationPermission.setOnClickListener(this);
        accessibilityPermission.setOnClickListener(this);
        contactsPermission.setOnClickListener(this);
        mirrorPermission.setOnClickListener(this);
        popupPermission.setOnClickListener(this);
        smsPermission.setOnClickListener(this);
        usagePermission.setOnClickListener(this);
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildProfileActivity.class);
            startActivity(intent);
        });

        if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT))) {
            popupPermission.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.permission_location) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }

            } else {
                turnOnPermission(v);
            }
        } else if (v.getId() == R.id.permission_notification) {

            String prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            boolean notificationEnabled = (prefString != null && prefString.contains(this.getPackageName()));
            if (!notificationEnabled)
                startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));

        } else if (v.getId() == R.id.permission_accessibility) {

            String prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            boolean accessibilityEnabled = (prefString != null && prefString.contains(this.getPackageName() + "/" + MessageGrabService.class.getName()));
            if (!accessibilityEnabled)
                startActivity(new Intent(ACTION_ACCESSIBILITY_SETTINGS));

        } else if (v.getId() == R.id.permission_contacts) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            } else {
                turnOnPermission(v);
            }

        } else if (v.getId() == R.id.permission_mirror) {

            MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_MIRROR_PERMISSION_CODE);
        } else if (v.getId() == R.id.permission_popup) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", getPackageName());
            startActivity(intent);

        } else if (v.getId() == R.id.permission_sms) {
            boolean readPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED;

            if (readPerm) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        1);
            }else{
                turnOnPermission(v);
            }
        } else if (v.getId() == R.id.permission_usage) {

            if (!usageAccessGranted()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }else{
                turnOnPermission(v);
            }
        }

    }

    private boolean usageAccessGranted(){
        try {
            PackageManager packageManager = this.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private void turnOnPermission(View v) {
        ViewGroup relativeView = (ViewGroup) ((ViewGroup) v).getChildAt(0);
        relativeView.setBackgroundColor(getColor(R.color.dark_green));
        relativeView.getChildAt(2).setVisibility(View.VISIBLE);
    }

    private void turnOffPermission(View v) {
        ViewGroup relativeView = (ViewGroup) ((ViewGroup) v).getChildAt(0);
        relativeView.setBackgroundColor(getColor(R.color.dark_red));
        relativeView.getChildAt(2).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_MIRROR_PERMISSION_CODE && resultCode == RESULT_OK) {
            turnOnPermission(mirrorPermission);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String prefString = null;

        boolean locationEnabled = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean accessibilityEnabled = false;
        prefString = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        boolean notificationEnabled = (prefString != null && prefString.contains(this.getPackageName()));
        boolean contactsEnabled = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean canDrawOver = Settings.canDrawOverlays(this);
        boolean readPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;


        prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        accessibilityEnabled = (prefString != null && prefString.contains(this.getPackageName() + "/" + MessageGrabService.class.getName()));


        if (locationEnabled)
            turnOnPermission(locationPermission);
        else turnOffPermission(locationPermission);

        if (accessibilityEnabled)
            turnOnPermission(accessibilityPermission);
        else turnOffPermission(accessibilityPermission);

        if (notificationEnabled)
            turnOnPermission(notificationPermission);
        else turnOffPermission(notificationPermission);

        if (contactsEnabled)
            turnOnPermission(contactsPermission);
        else turnOffPermission(contactsPermission);

        if (canDrawOver)
            turnOnPermission(popupPermission);
        else turnOffPermission(popupPermission);

        if(readPerm)
            turnOnPermission(smsPermission);
        else turnOffPermission(smsPermission);

        if(usageAccessGranted())
            turnOnPermission(usagePermission);
        else turnOffPermission(usagePermission);
    }
}