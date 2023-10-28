package com.licenta.monitorizareavioane.planepics;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class PlanespotterRepository {
    private String pictureURL;

    public void getURL(String icao24) {
        Call<ResponseBody> callURL = RetrofitClientPlanespotter.getInstance().getPlanespotterApi().getPlanePic(icao24);
        try {

            Response<ResponseBody> response = callURL.execute();
            ResponseBody responseBody = response.body();
            JSONObject jsonObject = new JSONObject(responseBody.string());
            if (jsonObject != null) {
                pictureURL = jsonObject
                        .getJSONArray("photos")
                        .getJSONObject(0)
                        .getJSONObject("thumbnail_large")
                        .getString("src");

                System.out.println("picture url " + pictureURL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getPictureURL() {
        return pictureURL;
    }
}
