package com.infernalbitsoft.guardianangel.Services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infernalbitsoft.guardianangel.Activities.AppLockActivity;
import com.infernalbitsoft.guardianangel.Activities.AppRestrictedActivity;
import com.infernalbitsoft.guardianangel.Activities.ProjectionActivity;
import com.infernalbitsoft.guardianangel.BroadcastReceiver.DeviceWakeUpReceiver;
import com.infernalbitsoft.guardianangel.Model.AppInfoClass;
import com.infernalbitsoft.guardianangel.Model.AppLimitClass;
import com.infernalbitsoft.guardianangel.Model.ContactsClass;
import com.infernalbitsoft.guardianangel.Model.GADatabase;
import com.infernalbitsoft.guardianangel.Model.MessageClass;
import com.infernalbitsoft.guardianangel.Model.SMSClass;
import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Utilities.GeofenceHelper;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.firebase.database.ServerValue.TIMESTAMP;
import static com.infernalbitsoft.guardianangel.BroadcastReceiver.DeviceWakeUpReceiver.isIsScreenOn;
import static com.infernalbitsoft.guardianangel.Services.MessageGrabService.isMessageGrabServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.NotificationListener.isNotificationListenerRunning;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.RealLog;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class CompoundService extends Service {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private SharedPreferences preferences;
    public static String currentApp = "";

    private Handler handler = new Handler();

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private Map<String, Long> appLimits = new HashMap<>();


    public static boolean isCompoundServiceRunning = false;


    //Listeners
    private ChildEventListener appUsageChildLimitListener = null;
    private ValueEventListener appPinValueEventListener = null;
    private ChildEventListener geofenceChildEventListener = null;
    private ValueEventListener killCommandValueEventListener = null;
    private ContentObserver smsContentObserver = null;
    private ValueEventListener presenceListener = null;
    private DeviceWakeUpReceiver wakeUpReceiver = new DeviceWakeUpReceiver();
    private IntentFilter screenStateFilter = null;
//    private boolean continueRunnable = true;

    //Handler Messages
    private CustomRunnable customRunnable = null;
    private MirrorServiceRunnable mirrorServiceRunnable = null;
    private SMSUpdateRunnable smsUpdateRunnable = null;
    private MessageUpdateRunnable messageUpdateRunnable = null;
    private OpenedAppRunnable openedAppRunnable = null;
    private AppLimitChecker appLimitChecker = null;

    //Hold the current connection status of phone with firebase
    boolean isConnected = false;

    //Online/Offline Objects
    Map<String, Object> isOnline = new HashMap<>();
    Map<String, Object> isOffline = new HashMap<>();

    //Last App String for checking which app was last executed
    public static String lastApp = "";

    public CompoundService() {
        isOnline.put("state", "offline");
        isOffline.put("state", "online");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log("CompoundService", "Runs");
//        Toast(this, "CompoundServiceRuns");
        preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

        isCompoundServiceRunning = true;
        isMessageGrabServiceRunning = true;
        isNotificationListenerRunning = true;


        database.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("KillCommand").setValue(false);

        handlePresence();
        RegisterWakeUpListener();
        RegisterAppLimitListener();
        RegisterPinCodeListener();
        RegisterServiceKillListener();

        geofenceHelper = new GeofenceHelper(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        RegisterGeofence();

        handler.post(new CompoundService.CustomRunnable(this));
        handler.post(new CompoundService.MirrorServiceRunnable(this));
        handler.post(new CompoundService.SMSUpdateRunnable(this));
        handler.post(new CompoundService.OpenedAppRunnable(this));
        handler.post(new MessageUpdateRunnable(this));
        handler.post(new AppLimitChecker(this));

        handleOutgoingSMS();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Toast(this, "Compound Service Removed");
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

    private void RegisterWakeUpListener() {
        screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(wakeUpReceiver, screenStateFilter);
    }

    private void RegisterAppLimitListener() {

        appUsageChildLimitListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                appLimits.put(snapshot.getKey(), (Long) snapshot.getValue());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                appLimits.put(snapshot.getKey(), (Long) snapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                appLimits.remove(snapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("AppUsageLimit").addChildEventListener(appUsageChildLimitListener);
    }

    private void RegisterPinCodeListener() {

        appPinValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String newPin = (String) snapshot.getValue();

                preferences.edit().putString(getString(R.string.sharedpreferences_set_pin_key), newPin).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("ChildPin").addValueEventListener(appPinValueEventListener);
    }

    private void RegisterGeofence() {

        geofenceChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log("GeofenceAdded", snapshot.getKey());

                Map<String, Object> geofence = (Map<String, Object>) snapshot.getValue();
                LatLng latLng = new LatLng((double) geofence.get("lat"), (double) geofence.get("lon"));
                Long radius = (Long) geofence.get("radius");
                String name = (String) geofence.get("name");

                addGeofence(snapshot.getKey().concat("<<>>").concat(name), latLng, radius);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log("GeofenceRemoved", snapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            private void addGeofence(String id, LatLng latlng, float radius) {

                Geofence geofence = geofenceHelper
                        .getGeofence(id, latlng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

                GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

                PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                if (ActivityCompat.checkSelfPermission(CompoundService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                            .addOnSuccessListener(aVoid -> {
                                RealLog("GeofenceAdded", "Geofence Added");
                            })
                            .addOnFailureListener(e -> {
                                String errorMessage = geofenceHelper.getGeofenceError(e);
                                RealLog("GeofenceError", errorMessage);
                            });
            }
        };

        geofencingClient.removeGeofences(geofenceHelper.getPendingIntent());
        database.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("Geofence").addChildEventListener(geofenceChildEventListener);
    }

    private void RegisterServiceKillListener() {

        killCommandValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() == null)
                    return;

                boolean killCommand = (boolean) snapshot.getValue();
                if (killCommand) {
                    stopService(new Intent(CompoundService.this, InstalledAppService.class));
                    stopService(new Intent(CompoundService.this, LocationService.class));
                    stopService(new Intent(CompoundService.this, MessageGrabService.class));
                    stopService(new Intent(CompoundService.this, NotificationListener.class));
                    stopService(new Intent(CompoundService.this, ScreenCaptureService.class));
                    stopSelf();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.child(user.getUid())
                .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                .child("KillCommand").addValueEventListener(killCommandValueEventListener);
    }

    private void handleOutgoingSMS() {

        smsContentObserver = new ContentObserver(handler) {

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Log("MessageSelfChange", "Works");
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                Log("MessageOnChange", "onChange " + uri.toString());
                Cursor cur = CompoundService.this.getContentResolver().query(uri, null, null, null, null);

                if(cur == null)
                    return;
                cur.moveToNext();
                try {
                    int id = Integer.parseInt(cur.getString(cur.getColumnIndex("_id")));
                    int type = Integer.parseInt(cur.getString(cur.getColumnIndex("type")));
                    String add = cur.getString(cur.getColumnIndex("address"));
                    long date = Long.parseLong(cur.getString(cur.getColumnIndex("date")));
                    String msg = cur.getString(cur.getColumnIndex("body"));

                    long prevID = preferences.getLong(getString(R.string.sharedpreferences_last_sms_id), -1);
                    if (prevID != id) {
                        Log("Message", "ID: " + id + "\nType: " + type + "\n Sender: " + add + "\n Date: " + date + "\n Message: " + msg);
                        preferences.edit().putLong(getString(R.string.sharedpreferences_last_sms_id), id).commit();
                        GADatabase db = GADatabase.getInstance(CompoundService.this);
                        db.smsDAO().insertSMS(new SMSClass(msg, date, add, getDisplayName(add), type == 1));
                    }

                    cur.close();
                }catch (IllegalStateException e){
                    return;
                }
            }

            private String getDisplayName(String address) {
                if (ActivityCompat.checkSelfPermission(CompoundService.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                    return "";
                Cursor phones = getContentResolver().query(ContactsContract.
                        CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.
                            CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.
                            CommonDataKinds.Phone.NUMBER));
                    if (phoneNumber.equals(address)) {
                        return name;
                    }
                }
                phones.close();
                return "";
            }
        };

        ContentResolver contentResolver = this.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);
    }

    private void handlePresence() {

        presenceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (!connected) {
                    isConnected = false;
//                    Toast(CompoundService.this, "disconnected");
                    return;
                }

                database.child(user.getUid())
                        .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                        .child("status").onDisconnect().setValue(isOnline).addOnCompleteListener(aVoid -> {
                    database.child(user.getUid())
                            .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                            .child("status").setValue(isOffline);
                });
                isConnected = true;
//                Toast(CompoundService.this, "Connected");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.child(".info/connected").addValueEventListener(presenceListener);
    }

    private class CustomRunnable implements Runnable {

        private Service service;

        public CustomRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(service, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                getContacts();
        }

        private void getContacts() {
            GADatabase db = GADatabase.getInstance(service);

            Map<String, Map<String, String>> contactsMap = new HashMap<>();
            Cursor cur = service.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            int index = 0;
            while (cur.moveToNext()) {
                String name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String num = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Map<String, String> contact = new HashMap<>();
                contact.put("name", name);
                contact.put("number", num);
                contactsMap.put(String.valueOf(index++), contact);

                if (db.contactsDAO().getContactByNumber(num).size() == 0) {
                    db.contactsDAO().insertContact(new ContactsClass(name, num));
                    Log("Contact", name.concat(" added"));
                } else {
//                    Log("Contact", name.concat(" already added"));
                }
            }
            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("Contacts").setValue(contactsMap);
//            Toast(service, "Compound posted");

            customRunnable = new CompoundService.CustomRunnable(service);

            handler.postDelayed(customRunnable, 60000);
        }
    }

    private class MirrorServiceRunnable implements Runnable {

        private Service service;

        public MirrorServiceRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            if (isIsScreenOn() && isConnected) {
                startService(new Intent(service, ScreenCaptureService.class));
//                Toast(service, "Mirror posted");
            }

            mirrorServiceRunnable = new MirrorServiceRunnable(service);
            handler.postDelayed(mirrorServiceRunnable, 20000);
        }
    }

    private class SMSUpdateRunnable implements Runnable {

        private Service service;

        public SMSUpdateRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            if (isConnected) {
                GADatabase db = GADatabase.getInstance(CompoundService.this);

                List<SMSClass> smss = db.smsDAO().getSMS();
                for (SMSClass sms : smss) {
                    if (!isConnected)
                        break;

                    String number = sms.getSender().replaceAll("[^a-zA-Z0-9 -]", "");
                    Map<String, Object> message = new HashMap<>();
                    message.put("timestamp", sms.getTimestamp());
                    message.put("message", sms.getMessage());
                    message.put("sender", sms.getSender());
                    message.put("received", sms.isReceived());
                    database.child(user.getUid())
                            .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                            .child("SMSList").child(number).push().setValue(message);
                    database.child(user.getUid())
                            .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                            .child("SMSList").child(number).child("name").setValue(sms.getName()).addOnSuccessListener(aVoid -> {
                        db.smsDAO().deleteSMS(sms);
                    });
                }

//                Toast(service, "SMS posted");
            }

            smsUpdateRunnable = new SMSUpdateRunnable(service);
            handler.postDelayed(smsUpdateRunnable, 20000);
        }
    }

    private class MessageUpdateRunnable implements Runnable {

        private Service service;

        public MessageUpdateRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            if (isConnected) {
                GADatabase db = GADatabase.getInstance(CompoundService.this);

                List<MessageClass> messages = db.messageDAO().getMessages();
                for (MessageClass msg : messages) {
                    if (!isConnected)
                        break;

                    if (msg.isStored())
                        continue;

                    String chatName = msg.getChatname().replaceAll("[^a-zA-Z0-9 -]", "");
                    Map<String, Object> message = new HashMap<>();
                    message.put("chatname", msg.getChatname());
                    message.put("sendername", msg.getSendername());
                    message.put("message", msg.getMessage());
                    message.put("app", msg.getApp());
                    message.put("datetime", msg.getDateTime());
                    message.put("timestamp", msg.getTimestamp());
                    message.put("isgroup", msg.isIsgroup());
                    database.child(user.getUid())
                            .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                            .child("Messages").child(msg.getApp()).child(chatName).push().setValue(message);
                    database.child(user.getUid())
                            .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                            .child("Messages").child(msg.getApp()).child(chatName).child("isgroup").setValue(msg.isIsgroup());
                    db.messageDAO().setMessageStored(msg.id);

                }

//                Toast(service, "SMS posted");
            }

            messageUpdateRunnable = new MessageUpdateRunnable(service);
            handler.postDelayed(messageUpdateRunnable, 20000);
        }
    }

    private class OpenedAppRunnable implements Runnable {

        private Service service;

        public OpenedAppRunnable(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            currentApp = "NULL";
            UsageStatsManager usm = (UsageStatsManager) service.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }

            if (currentApp.equals("com.android.settings") && !currentApp.equals(lastApp)) {
                Intent appLockIntent = new Intent(service, AppLockActivity.class);
                appLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                appLockIntent.putExtra("APP_NAME", currentApp);
                startActivity(appLockIntent);
            }

            if (!currentApp.equals(lastApp))
                lastApp = "";


            Log("Current", "" + currentApp);

            openedAppRunnable = new OpenedAppRunnable(service);
            handler.postDelayed(openedAppRunnable, 500);
        }
    }

    private class AppLimitChecker implements Runnable {

        private Service service;
        private int runCount = 0;
        private String prevApp = "xx";

        AppLimitChecker(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            for (String key : appLimits.keySet())
                Log("appLimitVal", key + " " + appLimits.get(key));


            Map<String, Long> usage = UsageStats();
            if (appLimits.containsKey(prevApp)
                    && usage.containsKey(prevApp)
                    && usage.get(prevApp) >= appLimits.get(prevApp)) {

                Intent appRestricted = new Intent(service, AppRestrictedActivity.class);
                appRestricted.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(appRestricted);

            }

            appLimitChecker = new AppLimitChecker(service);
            handler.postDelayed(appLimitChecker, 5000);
        }

        private Map<String, Long> UsageStats() {

            LocalDate today = new LocalDate();
            long todayMilli = today.toDate().getTime();
            long currTime = System.currentTimeMillis();

            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(todayMilli, currTime);

            Map<String, Long> eventUsage = new HashMap<>();
            Map<String, Long> totalUsage = new HashMap<>();

            while (usageEvents.hasNextEvent()) {
                UsageEvents.Event currentEvent = new UsageEvents.Event();
                usageEvents.getNextEvent(currentEvent);

                String app = currentEvent.getPackageName().replace(".", "");
                Long time = currentEvent.getTimeStamp();

                if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED
                        || currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {

                    if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                        eventUsage.put(app, time);
                        prevApp = app;
                    } else {
                        if (eventUsage.containsKey(app))
                            if (totalUsage.containsKey(app))
                                totalUsage.put(app, totalUsage.get(app) + (time - eventUsage.get(app)));
                            else
                                totalUsage.put(app, (time - eventUsage.get(app)));
                        eventUsage.remove(app);
                    }
                }
            }

            if (eventUsage.containsKey(prevApp))
                if (totalUsage.containsKey(prevApp))
                    totalUsage.put(prevApp, totalUsage.get(prevApp) + (System.currentTimeMillis() - eventUsage.get(prevApp)));
                else
                    totalUsage.put(prevApp, (System.currentTimeMillis() - eventUsage.get(prevApp)));

//            for (String key : totalUsage.keySet()) {
//                Log("TestTimeUsed", key + " " + totalUsage.get(key));
//            }

            return totalUsage;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast(this, "Compound Service stopped");

        if (appUsageChildLimitListener != null)
            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("AppUsageLimit").removeEventListener(appUsageChildLimitListener);

        if (appPinValueEventListener != null)
            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("ChildPin").removeEventListener(appPinValueEventListener);

        if (geofenceChildEventListener != null)
            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("Geofence").removeEventListener(geofenceChildEventListener);

        if (killCommandValueEventListener != null)
            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("KillCommand").removeEventListener(killCommandValueEventListener);

        if (smsContentObserver != null)
            this.getContentResolver().unregisterContentObserver(smsContentObserver);

        if (presenceListener != null) {
            database.child(user.getUid())
                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("status").setValue(isOnline);
            database.child(".info/connected").removeEventListener(presenceListener);
        }

        if (screenStateFilter != null)
            unregisterReceiver(wakeUpReceiver);

        if(customRunnable != null)
            handler.removeCallbacks(customRunnable);

        if(mirrorServiceRunnable != null)
            handler.removeCallbacks(mirrorServiceRunnable);

        if(smsUpdateRunnable != null)
            handler.removeCallbacks(smsUpdateRunnable);

        if(messageUpdateRunnable != null)
            handler.removeCallbacks(messageUpdateRunnable);

        if(openedAppRunnable != null)
            handler.removeCallbacks(openedAppRunnable);

        if(appLimitChecker != null)
            handler.removeCallbacks(appLimitChecker);

        isCompoundServiceRunning = false;
        isMessageGrabServiceRunning = false;
        isNotificationListenerRunning = false;
    }
}
