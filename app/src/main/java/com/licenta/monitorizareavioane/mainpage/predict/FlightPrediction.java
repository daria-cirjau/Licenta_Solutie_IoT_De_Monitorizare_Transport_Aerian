package com.licenta.monitorizareavioane.mainpage.predict;

public class FlightPrediction {
    private String Origin;
    private String Dest;
    private String IATA_Code_Operating_Airline;
    private float DepTime;
    private float ArrTime;
    private float DayOfWeek;
    private float DayofMonth;
    private int Year;
    private int Month;
    private float predictedDelay;
    private String airline;

    public FlightPrediction(String departure, String arrival, String IATA_Code_Operating_Airline, float depTime, float arrTime) {
        this.Origin = departure;
        this.Dest = arrival;
        this.IATA_Code_Operating_Airline = IATA_Code_Operating_Airline;
        this.DepTime = depTime;
        this.ArrTime = arrTime;
    }

    public String getOrigin() {
        return Origin;
    }

    public String getArrival() {
        return Dest;
    }

    public String getIATA_Code_Operating_Airline() {
        return IATA_Code_Operating_Airline;
    }

    public void setPredictedDelay(float predictedDelay) {
        this.predictedDelay = predictedDelay;
    }

    public float getDepTime() {
        return DepTime;
    }

    public float getArrTime() {
        return ArrTime;
    }

    public float getPredictedDelay() {
        return predictedDelay;
    }

    public void setDayOfWeek(float dayOfWeek) {
        this.DayOfWeek = dayOfWeek;
    }

    public void setDayofMonth(float dayofMonth) {
        this.DayofMonth = dayofMonth;
    }

    public void setYear(int year) {
        this.Year = year;
    }

    public void setMonth(int month) {
        this.Month = month;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getAirline() {
        return airline;
    }
}
