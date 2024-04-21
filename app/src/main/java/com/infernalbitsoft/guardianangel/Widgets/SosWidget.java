package com.infernalbitsoft.guardianangel.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.SOSLocationService;
import com.infernalbitsoft.guardianangel.Utilities.LogAndToast;

import static android.content.Context.MODE_PRIVATE;
import static com.infernalbitsoft.guardianangel.Services.SOSLocationService.latitude;
import static com.infernalbitsoft.guardianangel.Services.SOSLocationService.longitude;
import static com.infernalbitsoft.guardianangel.Services.SOSLocationService.timestamp;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class SosWidget extends AppWidgetProvider {

    private static final String SOS_CLICKED    = "automaticWidgetSosButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName sosWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.sos_widget);
        sosWidget = new ComponentName(context, SosWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.sos_widget_button, getPendingSelfIntent(context, SOS_CLICKED));
        appWidgetManager.updateAppWidget(sosWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getString(R.string.app_preferences), MODE_PRIVATE);

        if (SOS_CLICKED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.sos_widget);
            watchWidget = new ComponentName(context, SosWidget.class);

            String message = "Send Help!";
            if(timestamp != null &&
                    longitude != null &&
                    latitude != null){
//                message = timestamp + " " + longitude + " " + latitude;
                message = message.concat("\nLast Location: ")
                        .concat("https://www.google.com/maps/place/")
                        .concat(latitude.toString())
                        .concat(",")
                        .concat(longitude.toString())
                        .concat("\nat ")
                        .concat(timestamp);
            }

            Log.d("WidgetPress","Works " + message);

            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage("+923489234645", null, message, null, null);

            boolean isNumberAvailable = false;
            for(int i = 1; i <= 5; i++){
                String number = sharedpreferences.getString("CONTACT_" + i, "");
                if(!number.equals("")){
                    smsManager.sendTextMessage(number, null, message, null, null);
                    isNumberAvailable = true;
                    LogAndToast.Log("MessageSent", "Message Sent To: " + number);
                }
            }



            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


}