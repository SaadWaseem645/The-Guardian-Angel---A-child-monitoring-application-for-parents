package com.infernalbitsoft.guardianangel.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GeofenceDAO {

    @Query("Select * from Geofence")
    List<GeofenceClass> getGeofences();

    @Query("Select * From Geofence Where geofence_id = :id;")
    List<GeofenceClass> getGeofence(String id);

    @Insert
    void insertGeofence(GeofenceClass geofenceClass);

    @Update
    void updateGeofence(GeofenceClass geofenceClass);

    @Delete
    void deleteGeofence(GeofenceClass geofenceClass);

}
