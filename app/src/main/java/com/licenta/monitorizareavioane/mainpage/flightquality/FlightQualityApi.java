package com.licenta.monitorizareavioane.mainpage.flightquality;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FlightQualityApi {
    @GET("/getStatistics")
    Call<ResponseBody> getStatistics();
}
