package com.licenta.monitorizareavioane.aircraftdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "aircrafts")
public class AircraftInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "icao24")
    private String icao24;

    @ColumnInfo(name = "model")
    private String model;

    @ColumnInfo(name = "manufacturer")
    private String manufacturer;

    @ColumnInfo(name = "registration")
    private String registration;

    public AircraftInfo(String icao24, String model, String manufacturer, String registration) {
        this.icao24 = icao24;
        this.model = model;
        this.manufacturer = manufacturer;
        this.registration = registration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcao24() {
        return icao24;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }
}
