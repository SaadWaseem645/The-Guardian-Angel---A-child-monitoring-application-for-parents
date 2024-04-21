package com.infernalbitsoft.guardianangel.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.infernalbitsoft.guardianangel.R;

import java.util.HashMap;
import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class LocationService extends Service implements LocationListener {

    private final String LOCATION_PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;

    //Database Variables
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SharedPreferences preferences;

    public static boolean isLocationServiceRunning = false;

    public LocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        preferences = getSharedPreferences(getString(R.string.app_preferences),MODE_PRIVATE);
        isLocationServiceRunning = true;
        startLocationListner();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationListner() {
        Log("LocationService", "Location intent");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION_STRING)
                == PackageManager.PERMISSION_GRANTED) {
            Log("LocationService", "Location permission");
//            String provider = locationManager.getBestProvider(new Criteria(), false);
//            if (provider != null) {

                Log("LocationService", "Location provider");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
//            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Toast(this, "Location Removed");
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

    @Override
    public void onLocationChanged(Location location) {
        Log("LocationService", "Location");

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("lat", latitude);
        coordinates.put("lon", longitude);
        coordinates.put("timestamp", ServerValue.TIMESTAMP);

//        Toast(this, "Lat: " + latitude + "\nLon: " + longitude);


        if (user != null)
            database.child(user.getUid()).child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key),"")).child("Location").setValue(coordinates)
                    .addOnSuccessListener(aVoid -> {
//                        Toast(this, "Location Posted Successfully");
                    }).addOnFailureListener(e -> {
//                Toast(this, "Location Post failed " + e.getMessage());
            });

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast(this, "Location Service stopped");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);

        isLocationServiceRunning = false;
    }
}
