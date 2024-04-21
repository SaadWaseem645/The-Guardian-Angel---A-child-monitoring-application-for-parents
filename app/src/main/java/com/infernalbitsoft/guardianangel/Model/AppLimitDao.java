package com.infernalbitsoft.guardianangel.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppLimitDao {

    @Query("Select * from AppLimit")
    List<AppLimitClass> getAppLimit();


    @Query("Select * from AppLimit WHERE packageName = :pack")
    List<AppLimitClass> getAppLimit(String pack);

    @Query("UPDATE AppLimit SET time = 0")
    void resetAppLimit();

    @Query("UPDATE AppLimit SET time = :time WHERE packageName = :pack")
    void updateAppLimit(long time, String pack);

    @Insert
    void insertAppLimit(AppLimitClass appLimit);

    @Update
    void updateAppLimit(AppLimitClass appLimit);

    @Query("DELETE FROM AppLimit WHERE packageName = :pack")
    void deleteAppLimit(String pack);

    @Delete
    void deleteAppLimit(AppLimitClass appLimit);

}
