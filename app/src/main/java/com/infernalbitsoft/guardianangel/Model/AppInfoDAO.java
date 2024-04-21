package com.infernalbitsoft.guardianangel.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppInfoDAO {

    @Query("Select * from AppInfo")
    List<AppInfoClass> getAppInfo();

    @Query("Select * from AppInfo Where packageName = :packageName;")
    List<AppInfoClass> getAppInfoByName(String packageName);

    @Query("UPDATE AppInfo SET uninstalled = 1")
    void updateAppInfoUninstall();

    @Insert
    void insertAppInfo(AppInfoClass appInfo);

    @Update
    void updateAppInfo(AppInfoClass appInfo);

    @Delete
    void deleteAppInfo(AppInfoClass appInfo);

}
