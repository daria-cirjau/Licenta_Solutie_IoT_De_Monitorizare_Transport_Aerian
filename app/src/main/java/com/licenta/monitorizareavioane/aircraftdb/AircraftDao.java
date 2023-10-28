package com.licenta.monitorizareavioane.aircraftdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AircraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AircraftInfo aircraft);

    @Query("SELECT * FROM aircrafts")
    List<AircraftInfo> getAllAircrafts();

    @Insert
    void insertAll(AircraftInfo... aircraft);

    @Query("SELECT * FROM aircrafts WHERE icao24 = :icao24")
    AircraftInfo getAircraftByIcao24(String icao24);

    @Query("SELECT COUNT(*) FROM aircrafts")
    int getCount();

}
