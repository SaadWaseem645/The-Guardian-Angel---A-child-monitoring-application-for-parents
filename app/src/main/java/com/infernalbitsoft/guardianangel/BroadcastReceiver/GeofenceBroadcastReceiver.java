package com.infernalbitsoft.guardianangel.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infernalbitsoft.guardianangel.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {


    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast(context, "Geofence Triggered");


        preferences = context.getSharedPreferences(context.getString(R.string.app_preferences), MODE_PRIVATE);

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if (event.hasError()) {
            Log("GeofenceError", "onReceived: Geofence has error");
        }

        List<Geofence> geofenceList = event.getTriggeringGeofences();

        String triggeringFence = "";

//        for (Geofence geofence : geofenceList) {
//            if (!triggeringFence.equals(""))
//                triggeringFence = triggeringFence.concat("- ");
//            triggeringFence = triggeringFence.concat(geofence.getRequestId()
//                    .split("<<>>")[1]).concat(" ");
//        }

        int transitionType = event.getGeofenceTransition();

        Map<String, Object> geofenceEvent = new HashMap<>();
//        geofenceEvent.put("name", triggeringFence);
        geofenceEvent.put("timestamp", System.currentTimeMillis());

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
//                Toast(context, "Entered ".concat(triggeringFence));
                geofenceEvent.put("event", "Enter");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
//                Toast(context, "Exited ".concat(triggeringFence));
                geofenceEvent.put("event", "Exit");
                break;
        }

        for(Geofence geofence : geofenceList) {
            geofenceEvent.put("name", geofence.getRequestId().split("<<>>")[1]);
            database.child(user.getUid())
                    .child(preferences.getString(context.getString(R.string.sharedpreferences_child_profile_name_key), ""))
                    .child("GeofenceHistory").push().setValue(geofenceEvent);
        }
    }
}