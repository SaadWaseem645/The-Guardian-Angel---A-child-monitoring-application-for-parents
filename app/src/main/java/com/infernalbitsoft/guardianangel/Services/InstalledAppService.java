package com.infernalbitsoft.guardianangel.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Base64;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.Model.AppInfoClass;
import com.infernalbitsoft.guardianangel.Model.GADatabase;
import com.infernalbitsoft.guardianangel.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class InstalledAppService extends Service {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private SharedPreferences preferences;

    private Handler handler = new Handler();

    private Long phoneUsageToday = 0l;

    public static boolean isInstalledAppServiceRunning = false;


    //Handler messages
    private CustomRunnable customRunnable = null;
    private AppUsageRunnable appUsageRunnable = null;

    public InstalledAppService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log("InstalledApps", "Runs");
//        Toast(this, "installedAppServiceRuns");
        isInstalledAppServiceRunning = true;
        preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        handler.post(new CustomRunnable(this));
        handler.post(new AppUsageRunnable(this));
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Toast(this, "Installed App Removed");
        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    private class CustomRunnable implements Runnable {

        private Service service;

        public CustomRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            GADatabase db = GADatabase.getInstance(service);

            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);

            Map<String, Map<String, String>> appInfoMap = new HashMap<>();

            for (ApplicationInfo app : apps) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {

                    String name = pm.getApplicationLabel(app).toString();
                    String packageName = app.packageName;

                    Map<String, String> appData = new HashMap<>();
                    appData.put("packageName", packageName);
                    appData.put("appName", name);

                    byte[] byteArray = {};
                    try {
                        Drawable d = getPackageManager().getApplicationIcon(packageName);
                        Bitmap bitmap = drawableToBitmap(d);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                    } catch (PackageManager.NameNotFoundException e) {
                        return;
                    }

                    appData.put("icon", Base64.encodeToString(byteArray, Base64.DEFAULT));

                    appInfoMap.put(packageName.replace(".", ""), appData);


                    if (db.appInfoDAO().getAppInfoByName(packageName).size() == 0) {
                        db.appInfoDAO().insertAppInfo(new AppInfoClass(name, packageName, true));
//                    Log("InstalledApp", name.concat(" added"));
                    } else {
//                    Log("InstalledApp", name.concat(" already added"));
                    }
                }
            }

            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("AppList").setValue(appInfoMap);
//            Toast(service, "posted");

            customRunnable = new CustomRunnable(service);
            handler.postDelayed( customRunnable, 600000);
        }
    }

    private class AppUsageRunnable implements Runnable {

        private Service service;

        public AppUsageRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {

            LocalDate today = new LocalDate();
            Long todayMilli = today.toDate().getTime();
            Long weekMilli = today.minusDays(7).toDate().getTime();
            Log("TodayInMilli", todayMilli + "");

            long time = System.currentTimeMillis();

            Map<String, Object> usage = new HashMap<>();

            usage.put("timestamp", time);


            Map<String, Long> dailyUsage = UsageStats(todayMilli);
            Map<String, Long> weeklyUsage = UsageStats(weekMilli);

            UsageStats(todayMilli);

            for (String key : dailyUsage.keySet()) {

                String app = key.replace(".", "");

                Map<String, Long> use = new HashMap<>();
                use.put("daily", dailyUsage.get(key));

                usage.put(app, use);
            }

            for (String key : weeklyUsage.keySet()) {

                String app = key.replace(".", "");

                if (usage.containsKey(app))
                    ((Map<String, Long>) usage.get(app)).put("weekly", weeklyUsage.get(key));
                else {
                    Map<String, Long> use = new HashMap<>();
                    use.put("daily", 0L);
                    use.put("weekly", weeklyUsage.get(key));
                    usage.put(app, use);
                }
            }

            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("AppUsage").setValue(usage);


//            Toast(service, "posted app usage");

            appUsageRunnable = new AppUsageRunnable(service);
            handler.postDelayed(appUsageRunnable, 60000);
        }
    }


    private Map<String, Long> UsageStats(long startTime) {

        long currTime = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, currTime);

        Map<String, Long> eventUsage = new HashMap<>();
        Map<String, Long> totalUsage = new HashMap<>();

        String lastApp = "xx";

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);

            String app = currentEvent.getPackageName();
            Long time = currentEvent.getTimeStamp();

            if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED
                || currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {

                if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                    eventUsage.put(app, time);
                    lastApp = app;
                }
                else {
                    if (eventUsage.containsKey(app))
                        if (totalUsage.containsKey(app))
                            totalUsage.put(app, totalUsage.get(app) + (time - eventUsage.get(app)));
                        else
                            totalUsage.put(app, (time - eventUsage.get(app)));
                    eventUsage.remove(app);
                }
            }
        }

        if(eventUsage.containsKey(lastApp))
            if (totalUsage.containsKey(lastApp))
                totalUsage.put(lastApp, totalUsage.get(lastApp) + (System.currentTimeMillis() - eventUsage.get(lastApp)));
            else
                totalUsage.put(lastApp, (System.currentTimeMillis() - eventUsage.get(lastApp)));

//        for (String key : totalUsage.keySet()) {
//            Log("TestTimeUsed", key + " " + totalUsage.get(key));
//        }

        return totalUsage;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast(this, "Installed App Service stopped");

        if(customRunnable != null)
            handler.removeCallbacks(customRunnable);

        if(appUsageRunnable != null)
            handler.removeCallbacks(appUsageRunnable);

        isInstalledAppServiceRunning = false;
    }
}
