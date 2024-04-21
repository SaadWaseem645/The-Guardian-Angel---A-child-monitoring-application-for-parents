package com.infernalbitsoft.guardianangel.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SMS")
public class SMSClass {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "message")
    public String message;
    @ColumnInfo(name = "timestamp")
    public long timestamp;
    @ColumnInfo(name = "sender")
    public String sender;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "received")
    public boolean received;

    public SMSClass(String message, long timestamp, String sender, String name, boolean received) {
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.name = name;
        this.received = received;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }
}
