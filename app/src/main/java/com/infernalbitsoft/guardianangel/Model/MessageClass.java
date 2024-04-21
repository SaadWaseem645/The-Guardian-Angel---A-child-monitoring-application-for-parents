package com.infernalbitsoft.guardianangel.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Messages")
public class MessageClass {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "app")
    public String app;
    @ColumnInfo(name = "chatname")
    public String chatname;
    @ColumnInfo(name = "sendername")
    public String sendername;
    @ColumnInfo(name = "timestamp")
    public String timestamp;
    @ColumnInfo(name = "isgroup")
    public boolean isgroup;
    @ColumnInfo(name = "message")
    public String message;
    @ColumnInfo(name = "composite_id")
    public String composite_id;
    @ColumnInfo(name = "datetime")
    public long dateTime;
    @ColumnInfo(name = "stored")
    public boolean stored = false;

    public MessageClass(String app, String chatname, String sendername, String timestamp, boolean isgroup, String message) {
        this.app = app;
        this.chatname = chatname;
        this.sendername = sendername;
        this.timestamp = timestamp;
        this.isgroup = isgroup;
        this.message = message;
        this.composite_id = app+chatname+sendername+timestamp+isgroup+message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getChatname() {
        return chatname;
    }

    public void setChatname(String chatname) {
        this.chatname = chatname;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isIsgroup() {
        return isgroup;
    }

    public void setIsgroup(boolean isgroup) {
        this.isgroup = isgroup;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComposite_id() {
        return composite_id;
    }

    public void setComposite_id(String composite_id) {
        this.composite_id = composite_id;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }
}
