package com.infernalbitsoft.guardianangel.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MessageClass.class, AppInfoClass.class, ContactsClass.class, SMSClass.class, GeofenceClass.class, AppLimitClass.class}, exportSchema = false, version = 1)
public abstract class GADatabase extends RoomDatabase {

    private static final String DB_NAME = "GADatabase";
    private static GADatabase instance;

    public static synchronized GADatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), GADatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract MessageDAO messageDAO();
    public abstract AppInfoDAO appInfoDAO();
    public abstract ContactsDAO contactsDAO();
    public abstract SMSDAO smsDAO();
    public abstract GeofenceDAO geofenceDAO();
    public abstract AppLimitDao appLimitDao();

}
