package com.infernalbitsoft.guardianangel.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppInfo")
public class AppInfoClass {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "packageName")
    public String packageName;
    @ColumnInfo(name = "uninstalled")
    public boolean uninstalled;
    @ColumnInfo(name = "appUsage")
    public long appUsage;
    @ColumnInfo(name = "lastUsed")
    public long lastUsed;

    public AppInfoClass(String name, String packageName, Boolean uninstalled) {
        this.name = name;
        this.packageName = packageName;
        this.uninstalled = uninstalled;
        this.appUsage = 0;
        this.lastUsed = 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getAppUsage() {
        return appUsage;
    }

    public void setAppUsage(long appUsage) {
        this.appUsage = appUsage;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public boolean isUninstalled() {
        return uninstalled;
    }

    public void setUninstalled(boolean uninstalled) {
        this.uninstalled = uninstalled;
    }
}
