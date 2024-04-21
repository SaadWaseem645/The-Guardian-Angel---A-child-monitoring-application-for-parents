package com.infernalbitsoft.guardianangel.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SMSDAO {

    @Query("Select * from SMS")
    List<SMSClass> getSMS();

    @Insert
    void insertSMS(SMSClass smsClass);

    @Update
    void updateSMS(SMSClass smsClass);

    @Delete
    void deleteSMS(SMSClass smsClass);

}
