package com.infernalbitsoft.guardianangel.Services;

import android.app.Notification;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.infernalbitsoft.guardianangel.Model.GADatabase;
import com.infernalbitsoft.guardianangel.Model.MessageClass;
import com.infernalbitsoft.guardianangel.Utilities.LogAndToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "NotificationListener";
    private static final String WA_PACKAGE = "com.whatsapp";
    private static final String M_PACKAGE = "com.facebook.orca";

    public static boolean isNotificationListenerRunning = true;

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Notification Listener connected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        Toast(this, "Notification service is Running");

        Log("NotificationListener","Notification Received");

        if(isNotificationListenerRunning) {
            if (AccessibilityPermission())
                if (sbn.getPackageName().equals(WA_PACKAGE))
                    whatsappNotificationFilter(sbn);
                else if (sbn.getPackageName().equals(M_PACKAGE))
                    messengerNotificationFilter(sbn);
        }

    }

    private boolean AccessibilityPermission() {
        boolean accessibilityEnabled = false;
        String prefString = null;
        prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        accessibilityEnabled = (prefString != null && prefString.contains(this.getPackageName() + "/" + MessageGrabService.class.getName()));
        return accessibilityEnabled;
    }

    private void whatsappNotificationFilter(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Bundle bundle = notification.extras;
        int flag = notification.flags;
        if (flag != 8)
            return;

        String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
        String msg = bundle.getString(NotificationCompat.EXTRA_TEXT);
        long time = notification.when;

        Log.i(TAG, "Flag: " + flag);
        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Message: " + msg);
        Log.i(TAG, "Time: " + time);

        String chatName = "";
        String senderName = "";
        String timestamp = "";
        Boolean isGroup = false;


        if (from.contains(": ")) {
            String[] tempFrom = from.split(": ");
            senderName = tempFrom[1];
            chatName = tempFrom[0].replaceAll(" \\([^)]*\\)", "");
            isGroup = true;
        } else {
            senderName = from;
            chatName = from;
        }

        Date date = new Date(time);
        DateFormat format = new SimpleDateFormat("h:mm aa");
        format.setTimeZone(TimeZone.getDefault());
        timestamp = format.format(date);


        GADatabase db = GADatabase.getInstance(this);
        MessageClass message = new MessageClass("WhatsApp", chatName, senderName, timestamp, isGroup, msg);
        message.setDateTime(time);

//        Log("Message",message.app+message.chatname+message.sendername+message.timestamp+message.isgroup+message.message);

        db.messageDAO().insertMessage(message);
    }

    private void messengerNotificationFilter(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Bundle bundle = notification.extras;
        int flag = notification.flags;
        if (flag != 1 && flag != 9)
            return;

        String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
        String msg = bundle.getString(NotificationCompat.EXTRA_TEXT);
        long time = notification.when;

        Log.i(TAG, "Flag: " + flag);
        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Message: " + msg);
        Log.i(TAG, "Time: " + time);


        String chatName = "";
        String senderName = "";
        String timestamp = "";
        Boolean isGroup = false;


        if (from.contains(": ")) {
            String[] tempFrom = from.split(": ");
            senderName = tempFrom[1];
            chatName = tempFrom[0].replaceAll(" \\([^)]*\\)", "");
            isGroup = true;
        } else {
            senderName = from;
            chatName = from;
        }

        Date date = new Date(time);
        DateFormat format = new SimpleDateFormat("h:mm aa");
        format.setTimeZone(TimeZone.getDefault());
        timestamp = format.format(date);


        GADatabase db = GADatabase.getInstance(this);
        MessageClass message = new MessageClass("Messenger", chatName, senderName, timestamp, isGroup, msg);
        message.setDateTime(time);

        db.messageDAO().insertMessage(message);
    }
}
