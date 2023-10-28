package com.licenta.monitorizareavioane.aircraftdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AircraftInfo.class}, version = 1)
public abstract class AircraftDatabase extends RoomDatabase {
    private AircraftDao aircraftDao;

    public abstract AircraftDao getAircraftDao();
}
