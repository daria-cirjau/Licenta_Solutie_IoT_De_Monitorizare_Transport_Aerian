package com.licenta.monitorizareavioane.favoritesdb;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass(name = "favorites")
public class Favorites extends RealmObject {
    @io.realm.annotations.PrimaryKey
    @NotNull
    @Index
    private String _id;
    @Required
    private String flightNumber;
    private String userId;
    private String departureTime;
    private String arrivalTime;
    private String planePictureData;
    private String departureLocation;
    private String arrivalLocation;

    public Favorites() {

    }

    public Favorites(@NotNull String _id, String flightNumber, String departureTime, String arrivalTime, String planePictureData, String departureLocation, String arrivalLocation, String userId) {
        this._id = _id;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.planePictureData = planePictureData;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.userId = userId;
        this.flightNumber = flightNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @NotNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NotNull String _id) {
        this._id = _id;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getPlanePictureData() {
        return planePictureData;
    }

    public void setPlanePictureData(String planePictureData) {
        this.planePictureData = planePictureData;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}
