package com.infernalbitsoft.guardianangel.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class DeviceWakeUpReceiver extends BroadcastReceiver {

    public static boolean isScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            isScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
           isScreenOn = true;
        }
    }

    public static boolean isIsScreenOn(){
        return isScreenOn;
    }
}