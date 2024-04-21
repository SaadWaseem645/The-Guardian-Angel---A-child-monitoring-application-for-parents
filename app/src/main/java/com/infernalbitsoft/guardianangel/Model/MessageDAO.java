package com.infernalbitsoft.guardianangel.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDAO {

    @Query("Select * from Messages")
    List<MessageClass> getMessages();

    @Query("Select * from Messages WHERE app = 'WhatsApp'")
    List<MessageClass> getWhatsAppMessages();

    @Query("SELECT * FROM (SELECT * FROM Messages WHERE app = :appName AND chatname = :chatname ORDER BY id DESC LIMIT 10) ORDER BY id ASC;")
    List<MessageClass> getLastAppMessages(String appName, String chatname);

    @Query("UPDATE Messages SET stored = 1 WHERE id = :id")
    void setMessageStored(long id);

    @Insert
    void insertMessage(MessageClass message);

    @Update
    void updateMessage(MessageClass message);

    @Delete
    void deleteMessage(MessageClass message);

}
