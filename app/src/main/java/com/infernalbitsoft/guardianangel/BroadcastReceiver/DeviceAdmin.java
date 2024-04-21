package com.infernalbitsoft.guardianangel.BroadcastReceiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.infernalbitsoft.guardianangel.R;

import static android.content.Context.MODE_PRIVATE;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;

public class DeviceAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log("AdminReceiver", "admin_receiver_status_enabled");

        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getString(R.string.app_preferences), MODE_PRIVATE);
        sharedpreferences.edit().putBoolean(context.getString(R.string.sharedpreferenced_admin), true).commit();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "admin_receiver_status_disable_warning";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log("AdminReceiver", "admin_receiver_status_disabled");

        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getString(R.string.app_preferences), MODE_PRIVATE);
        sharedpreferences.edit().putBoolean(context.getString(R.string.sharedpreferenced_admin), false).commit();
    }
}
