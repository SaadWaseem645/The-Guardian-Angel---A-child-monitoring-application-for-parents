package com.infernalbitsoft.guardianangel.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

import com.google.firebase.database.ServerValue;
import com.infernalbitsoft.guardianangel.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class SOSLocationService extends Service implements LocationListener {

    private final String LOCATION_PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;
    public static Double latitude = null;
    public static Double longitude = null;
    public static String timestamp = null;
    public static boolean isSOSLocationRunning = false;

    public SOSLocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isSOSLocationRunning = true;
        startLocationListner();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationListner() {
//        Toast(this, "SOS Location Started");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION_STRING)
                == PackageManager.PERMISSION_GRANTED) {
            Log("LocationService", "Location permission");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Toast(this, "SOS Location Removed");
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
        Log("SosLocationService", "Location");

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Date date = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        format.setTimeZone(TimeZone.getDefault());
        timestamp = format.format(date);

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

}