package com.infernalbitsoft.guardianangel.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactsDAO {

    @Query("Select * from Contacts")
    List<ContactsClass> getContacts();

    @Query("Select * from Contacts Where number = :number;")
    List<ContactsClass> getContactByNumber(String number);

    @Insert
    void insertContact(ContactsClass contact);

    @Update
    void updateContact(ContactsClass contact);

    @Delete
    void deleteContact(ContactsClass contact);
}
