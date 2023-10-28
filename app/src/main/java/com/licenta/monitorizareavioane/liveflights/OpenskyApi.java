package com.licenta.monitorizareavioane.liveflights;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenskyApi {

    String BASE_URL = "https://opensky-network.org/api/";

    @GET("states/all")
    Call<ResponseBody> getAllFlights();

    @GET("flights/all")
    Call<ResponseBody> getFlightsByDate(@Query("begin") long begin, @Query("end") long end);

    @GET("flights/aircraft")
    Call<ResponseBody> getFlightByDateAndIcao(@Query("icao24") String icao24, @Query("begin") long begin, @Query("end") long end);  //super pt istoric -> istoric pt favorite

    @GET("flights/arrival")
    Call<ResponseBody> getArrivalByAirport(@Query("airport") String icao, @Query("begin") long begin, @Query("end") long end);

    @GET("flights/departure")
    Call<ResponseBody> getDepartureByAirport(@Query("departure") String icao, @Query("begin") long begin, @Query("end") long end);

    @GET("tracks/all")
    Call<ResponseBody> trackFlight(@Query("icao24") String icao24, @Query("time") long time); //arata path-ul pt live tracking (e un JSONArray cu Waypoints of the trajectory), din nou e super pt istoric
    //pt ca merge pe 30 de zile + path live la click pe marker - vom folosi la filtrare


}