package com.licenta.monitorizareavioane.mainpage.flightquality;

public class FlightQualityModel {
    private String avgTemp;
    private String avgHum;
    private String minTemp;
    private String minTempDate;
    private String maxTemp;
    private String maxTempDate;
    private String minHum;
    private String minHumDate;
    private String maxHum;
    private String maxHumDate;
    private String vibrationPercent;
    private boolean isFinished;

    public FlightQualityModel(String avgTemp, String avgHum, String minTemp, String minTempDate, String maxTemp, String maxTempDate, String minHum, String minHumDate, String maxHum, String maxHumDate, String vibrationPercent, boolean isFinished) {
        this.avgTemp = avgTemp;
        this.avgHum = avgHum;
        this.minTemp = minTemp;
        this.minTempDate = minTempDate;
        this.maxTemp = maxTemp;
        this.maxTempDate = maxTempDate;
        this.minHum = minHum;
        this.minHumDate = minHumDate;
        this.maxHum = maxHum;
        this.maxHumDate = maxHumDate;
        this.vibrationPercent = vibrationPercent;
        this.isFinished = isFinished;
    }

    public String getAvgTemp() {
        return avgTemp;
    }

    public String getAvgHum() {
        return avgHum;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMinTempDate() {
        return minTempDate;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMaxTempDate() {
        return maxTempDate;
    }

    public String getMinHum() {
        return minHum;
    }

    public String getMinHumDate() {
        return minHumDate;
    }

    public String getMaxHum() {
        return maxHum;
    }

    public String getMaxHumDate() {
        return maxHumDate;
    }

    public String getVibrationPercent() {
        return vibrationPercent;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
