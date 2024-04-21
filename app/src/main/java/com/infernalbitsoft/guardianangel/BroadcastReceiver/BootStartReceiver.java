package com.infernalbitsoft.guardianangel.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.CompoundService;
import com.infernalbitsoft.guardianangel.Services.DataService;
import com.infernalbitsoft.guardianangel.Services.InstalledAppService;
import com.infernalbitsoft.guardianangel.Services.LocationService;
import com.infernalbitsoft.guardianangel.Services.MessageGrabService;

import static android.content.Context.MODE_PRIVATE;
import static com.infernalbitsoft.guardianangel.Services.CompoundService.isCompoundServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.InstalledAppService.isInstalledAppServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.LocationService.isLocationServiceRunning;
import static com.infernalbitsoft.guardianangel.Services.MessageGrabService.isMessageGrabServiceRunning;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class BootStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intentArg) {
//        Intent intent = new Intent(context, LocationService.class);
//        Toast(context, "Boot Works");
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getString(R.string.app_preferences), MODE_PRIVATE);
        int activity = sharedpreferences.getInt(context.getString(R.string.sharedpreferenced_activity), 0);
        if(activity == 100){
            if(!isLocationServiceRunning)
                context.startService(new Intent(context, LocationService.class));

            if(!isInstalledAppServiceRunning)
                context.startService(new Intent(context, InstalledAppService.class));

            if(!isCompoundServiceRunning)
                context.startService(new Intent(context, CompoundService.class));

            if(!isMessageGrabServiceRunning)
                context.startService(new Intent(context, MessageGrabService.class));
        }
//        Log("BootStartReceiver_","Boot Works");
//        Intent intent = new Intent(context, BootStartActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//        context.startService(intent);
    }
}