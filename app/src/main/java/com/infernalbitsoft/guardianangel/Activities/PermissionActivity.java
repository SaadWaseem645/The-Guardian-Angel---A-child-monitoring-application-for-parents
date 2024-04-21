package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.MessageGrabService;

import java.util.List;
import java.util.Locale;

import static android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.infernalbitsoft.guardianangel.Utilities.GlobalVariables.SCREEN_MIRROR_PERMISSION_CODE;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class PermissionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PERMISSION_KEY = "PermissionStage";
    public static final String[] permissions = {
            "Location", "Notification", "Accessibility", "Contacts", "Mirror", "Popup", "Sms", "Usage", "Autostart", "Admin"
    };

    private String permission = "";
    private Button permissionEnable;
    private Button permissionContinue;

    private RelativeLayout layout;
    private ImageView permissionImage;
    private TextView permissionName;
    private TextView permissionSub;

    private boolean isAutoStart = false;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.permission_animation);

        layout = findViewById(R.id.permissionLayout);
        permissionImage = findViewById(R.id.permission_image);
        permissionEnable = findViewById(R.id.enable_permission_button);
        permissionContinue = findViewById(R.id.permission_continue);
        permissionName = findViewById(R.id.permission_name);
        permissionSub = findViewById(R.id.permission_subtext);

        permissionEnable.setOnClickListener(this);

        permission = getIntent().getStringExtra(PERMISSION_KEY);
        Log("PermissionName", permission);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        if (permission.equals(permissions[0])) {
            layout.setBackgroundResource(R.drawable.background_gradient_green);
            permissionImage.setImageResource(R.drawable.ic_location);
            permissionName.setText(R.string.location_permission);
            permissionSub.setText(R.string.location_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 0).commit();
        } else if (permission.equals(permissions[1])) {
            layout.setBackgroundResource(R.drawable.background_gradient_red);
            permissionImage.setImageResource(R.drawable.ic_notification);
            permissionName.setText(R.string.notification_permission);
            permissionSub.setText(R.string.notification_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 1).commit();
        } else if (permission.equals(permissions[2])) {
            layout.setBackgroundResource(R.drawable.background_gradient_blue);
            permissionImage.setImageResource(R.drawable.ic_permission_accessibility);
            permissionName.setText(R.string.accessibility_permission);
            permissionSub.setText(R.string.accessibility_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 2).commit();
        } else if (permission.equals(permissions[3])) {
            layout.setBackgroundResource(R.drawable.background_gradient_green);
            permissionImage.setImageResource(R.drawable.ic_permission_contact);
            permissionName.setText(R.string.contacts_permission);
            permissionSub.setText(R.string.contacts_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity) , 3).commit();
        } else if (permission.equals(permissions[4])) {
            layout.setBackgroundResource(R.drawable.background_gradient_red);
            permissionImage.setImageResource(R.drawable.ic_permission_mirror);
            permissionName.setText(R.string.mirror_permission);
            permissionSub.setText(R.string.mirror_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 4).commit();
        } else if (permission.equals(permissions[5])) {
            layout.setBackgroundResource(R.drawable.background_gradient_blue);
            permissionImage.setImageResource(R.drawable.ic_popup);
            permissionName.setText(R.string.popup_permission);
            permissionSub.setText(R.string.popup_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 5).commit();
        } else if (permission.equals(permissions[6])) {
            layout.setBackgroundResource(R.drawable.background_gradient_green);
            permissionImage.setImageResource(R.drawable.ic_permission_sms);
            permissionName.setText(R.string.sms_permission);
            permissionSub.setText(R.string.sms_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 6).commit();
        } else if (permission.equals(permissions[7])) {
            layout.setBackgroundResource(R.drawable.background_gradient_red);
            permissionImage.setImageResource(R.drawable.ic_permission_usage);
            permissionName.setText(R.string.usage_permission);
            permissionSub.setText(R.string.usage_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 7).commit();
        } else if (permission.equals(permissions[8])) {
            layout.setBackgroundResource(R.drawable.background_gradient_blue);
            permissionImage.setImageResource(R.drawable.ic_restart);
            permissionName.setText(R.string.autostart_permission);
            permissionSub.setText(R.string.autostart_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 8).commit();
        } else if (permission.equals(permissions[9])) {
            layout.setBackgroundResource(R.drawable.background_gradient_green);
            permissionImage.setImageResource(R.drawable.ic_admin);
            permissionName.setText(R.string.admin_permission);
            permissionSub.setText(R.string.admin_permission_description);

            sharedpreferences.edit().putInt(getString(R.string.sharedpreferenced_activity), 9).commit();
        }

        permissionImage.setAnimation(animation);

    }

    private void turnOnPermission() {
        intent = new Intent(this, PermissionActivity.class);

        permissionEnable.setVisibility(View.GONE);
        permissionContinue.setVisibility(View.VISIBLE);

        Pair[] pair = new Pair[3];
        pair[0] = new Pair<View, String>(permissionImage, "permission_image");
        pair[1] = new Pair<View, String>(permissionName, "permission_text");
        pair[2] = new Pair<View, String>(permissionSub, "permission_sub");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair);

        if (permission.equals(permissions[0])) {
            intent.putExtra(PERMISSION_KEY, permissions[1]);
        } else if (permission.equals(permissions[1])) {
            intent.putExtra(PERMISSION_KEY, permissions[2]);
        } else if (permission.equals(permissions[2])) {
            intent.putExtra(PERMISSION_KEY, permissions[3]);
        } else if (permission.equals(permissions[3])) {
            intent.putExtra(PERMISSION_KEY, permissions[4]);
        } else if (permission.equals(permissions[4])) {
            if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT)))
                intent.putExtra(PERMISSION_KEY, permissions[5]);
            else
                intent.putExtra(PERMISSION_KEY, permissions[6]);
        } else if (permission.equals(permissions[5])) {
            intent.putExtra(PERMISSION_KEY, permissions[6]);
        } else if (permission.equals(permissions[6])) {
            intent.putExtra(PERMISSION_KEY, permissions[7]);
        } else if (permission.equals(permissions[7])) {
            if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT)))
                intent.putExtra(PERMISSION_KEY, permissions[8]);
            else
                intent.putExtra(PERMISSION_KEY, permissions[9]);
        } else if (permission.equals(permissions[8])) {
            intent.putExtra(PERMISSION_KEY, permissions[9]);
        } else if (permission.equals(permissions[9])) {
            intent = new Intent(this, ChildProfileActivity.class);
        }

        permissionContinue.setOnClickListener(v -> {
            startActivity(intent, options.toBundle());
            finish();
        });
    }

    @Override
    public void onClick(View v) {
        if (permission.equals(permissions[0])) {
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
                turnOnPermission();
            }
        } else if (permission.equals(permissions[1])) {

            String prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            boolean notificationEnabled = (prefString != null && prefString.contains(this.getPackageName()));
            if (!notificationEnabled)
                startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));

        } else if (permission.equals(permissions[2])) {

            String prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            boolean accessibilityEnabled = (prefString != null && prefString.contains(this.getPackageName() + "/" + MessageGrabService.class.getName()));
            if (!accessibilityEnabled)
                startActivity(new Intent(ACTION_ACCESSIBILITY_SETTINGS));

        } else if (permission.equals(permissions[3])) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            } else {
                turnOnPermission();
            }

        } else if (permission.equals(permissions[4])) {

            MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_MIRROR_PERMISSION_CODE);
        } else if (permission.equals(permissions[5])) {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", getPackageName());
            startActivity(intent);

        } else if (permission.equals(permissions[6])) {
            boolean readPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED;

            if (readPerm) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        1);
            } else {
                turnOnPermission();
            }
        } else if (permission.equals(permissions[7])) {

            if (!usageAccessGranted()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
//                intent.setData(uri);
                startActivity(intent);
            } else {
                turnOnPermission();
            }
        }else if (permission.equals(permissions[8])) {
            addAutoStartup();
            isAutoStart = true;
        }else if (permission.equals(permissions[9])) {
            if(adminPermissionGranted())
                turnOnPermission();
            else{
                startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
                Log("AdminActivityCalled","Works");
            }
        }
    }

    private boolean usageAccessGranted() {
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

    private boolean adminPermissionGranted(){
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        return sharedpreferences.getBoolean(getString(R.string.sharedpreferenced_admin), false);
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_MIRROR_PERMISSION_CODE && resultCode == RESULT_OK) {
            turnOnPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permission.equals(permissions[0])) {

            boolean locationEnabled = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (locationEnabled) turnOnPermission();

        } else if (permission.equals(permissions[1])) {

            String prefString = null;
            prefString = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
            boolean notificationEnabled = (prefString != null && prefString.contains(this.getPackageName()));
            if (notificationEnabled) turnOnPermission();

        } else if (permission.equals(permissions[2])) {

            boolean accessibilityEnabled = false;
            String prefString = null;
            prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            accessibilityEnabled = (prefString != null && prefString.contains(this.getPackageName() + "/" + MessageGrabService.class.getName()));
            if (accessibilityEnabled) turnOnPermission();

        } else if (permission.equals(permissions[3])) {

            boolean contactsEnabled = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
            if (contactsEnabled) turnOnPermission();
        } else if (permission.equals(permissions[5])) {
            boolean canDrawOver = Settings.canDrawOverlays(this);
            if (canDrawOver) turnOnPermission();

        } else if (permission.equals(permissions[6])) {

            boolean readPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
            if (readPerm) turnOnPermission();

        } else if (permission.equals(permissions[7])) {

            if (usageAccessGranted()) turnOnPermission();

        } else if (permission.equals(permissions[8])) {

            if (isAutoStart) turnOnPermission();

        }else if (permission.equals(permissions[9])) {

            if (adminPermissionGranted()) turnOnPermission();

        }
    }
}