package com.infernalbitsoft.guardianangel.Utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LogAndToast {

    public static void Log(String tag, String msg){
        Log.d(tag, msg);
    }

    public static void RealLog(String tag, String msg){
        Log.d(tag, msg);
    }

    public static void Toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void RealToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
