package com.licenta.monitorizareavioane.mainpage.flightquality;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class FlightQualityRepository {
    private FlightQualityModel flightQualityModel;

    public void getInfo(Context context) {
        Call<ResponseBody> callURL = RetrofitClientFlightQuality.getInstance(context).getFlightQualityApi().getStatistics();
        try {
            Response<ResponseBody> response = callURL.execute();
            ResponseBody responseBody = response.body();
            JSONArray jsonArray = new JSONArray(response.body().string());
            if (jsonArray != null) {
                String avgTemp = jsonArray.getJSONObject(0).getString("temp_mean");
                String avgHum = jsonArray.getJSONObject(1).getString("hum_mean");
                String maxTemp = jsonArray.getJSONObject(2).getString("temp_max");
                String maxTempDate = jsonArray.getJSONObject(3).getString("time_temp_max");
                String maxHum = jsonArray.getJSONObject(4).getString("hum_max");
                String maxHumDate = jsonArray.getJSONObject(5).getString("time_hum_max");
                String minTemp = jsonArray.getJSONObject(6).getString("temp_min");
                String minTempDate = jsonArray.getJSONObject(7).getString("time_temp_min");
                String minHum = jsonArray.getJSONObject(8).getString("hum_min");
                String minHumDate = jsonArray.getJSONObject(9).getString("time_hum_min");
                String vibrationPercent = jsonArray.getJSONObject(10).getString("vibration");
                Boolean isFinished = jsonArray.getJSONObject(11).getBoolean("is_finished");
                flightQualityModel = new FlightQualityModel(avgTemp, avgHum, minTemp, minTempDate, maxTemp, maxTempDate, minHum, minHumDate, maxHum, maxHumDate, vibrationPercent, isFinished);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FlightQualityModel getFlightQualityModel(Context context) {
        getInfo(context);
        return flightQualityModel;
    }
}
