package com.infernalbitsoft.guardianangel.Services;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class DataService extends IntentService implements LocationListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = database.getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public DataService() {
        super("DataService");
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {

//        database.setLogLevel(Logger.Level.DEBUG);
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);




//        user.getIdToken(true).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Log.d("DataSerivee", "token=" + task.getResult().getToken());
//            } else {
//                Log.e("DataServii", "exception=" + task.getException().toString());
//            }
//        });

//        Context context = this;
//        Toast(this, "Dataservice ran");
//
//        Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                mDatabaseRef.child(user.getUid()).child("Saad").child("Messages").push().setValue("Hello")
//                        .addOnSuccessListener(aVoid -> {
//                            Toast(context, "Posted Successfully");
//                        }).addOnFailureListener(e -> {
//                    Toast(context, "Post failed " + e.getMessage());
//                });
//                handler.postDelayed(this, 60000);
////                Toast(context, "Dataservice is Running " + user.getUid() + " " + (database == null) + " " + (mDatabaseRef == null) + " " + (user == null));
//            }
//        };
//
//        if (user != null)
//            handler.postDelayed(runnable, 10000);
//        else {
//            Toast(this, "User not found");
//            Log("DataService_Poster", "User not found");
//        }
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                    30000,
//                    0, this);
//
//            Toast(this, "LOCATION RUNS");
//        }
//
//        return START_REDELIVER_INTENT;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast(this, "Data service destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Toast(this, "Dataservice is onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Context context = this;
//        Toast(this, "Dataservice ran");

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mDatabaseRef.child(user.getUid()).child("Saad").child("Messages").push().setValue("Hello")
                        .addOnSuccessListener(aVoid -> {
//                            Toast(context, "Posted Successfully");
                        }).addOnFailureListener(e -> {
//                    Toast(context, "Post failed " + e.getMessage());
                });
                handler.postDelayed(this, 60000);
//                Toast(context, "Dataservice is Running " + user.getUid() + " " + (database == null) + " " + (mDatabaseRef == null) + " " + (user == null));
            }
        };

        if (user != null)
            handler.postDelayed(runnable, 10000);
        else {
            Toast(this, "User not found");
            Log("DataService_Poster", "User not found");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    30000,
                    0, this);

//            Toast(this, "LOCATION RUNS");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("lat",latitude);
        coordinates.put("lon", longitude);

//        Toast(this, "Lat: " + latitude + "\nLon: " + longitude);



        mDatabaseRef.child(user.getUid()).child("Saad").child("Location").setValue(coordinates)
                .addOnSuccessListener(aVoid -> {
//                    Toast(this, "Location Posted Successfully");
                }).addOnFailureListener(e -> {
//            Toast(this, "Location Post failed " + e.getMessage());
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
}