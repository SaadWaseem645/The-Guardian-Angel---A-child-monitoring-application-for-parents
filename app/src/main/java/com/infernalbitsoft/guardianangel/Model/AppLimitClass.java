package com.infernalbitsoft.guardianangel.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppLimit")
public class AppLimitClass {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "packageName")
    public String packageName;
    @ColumnInfo(name = "time")
    public long time;

    public AppLimitClass(String packageName) {
        this.packageName = packageName;
        this.time = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
