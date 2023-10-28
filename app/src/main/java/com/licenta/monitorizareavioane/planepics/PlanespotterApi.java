package com.licenta.monitorizareavioane.planepics;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PlanespotterApi {
    String BASE_URL = "https://api.planespotters.net/pub/photos/hex/";

    @GET("{icao24}")
    Call<ResponseBody> getPlanePic(@Path("icao24") String icao24);
}

