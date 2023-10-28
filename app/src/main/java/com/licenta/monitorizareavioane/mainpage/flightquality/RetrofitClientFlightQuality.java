package com.licenta.monitorizareavioane.mainpage.flightquality;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.licenta.monitorizareavioane.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientFlightQuality {

    private static RetrofitClientFlightQuality instance = null;
    private FlightQualityApi flightQualityApi;
    private static Context context;

    private RetrofitClientFlightQuality() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(createBaseUrlCustomIP())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        flightQualityApi = retrofit.create(FlightQualityApi.class);
    }

    private String createBaseUrlCustomIP() {
        Activity currentActivity = (Activity) context;
        EditText moreInfoFragmentTxtIp = currentActivity.findViewById(R.id.moreInfoFragmentTxtIp);
        Properties props = getProperties();

        String ip = moreInfoFragmentTxtIp.getText().toString();
        String port = props.getProperty("rpi_port");

        return "http://" + ip + ":" + port;
    }


    private String createBaseUrl() {
        Properties props = getProperties();

        String ip = props.getProperty("rpi_ip_address");
        String port = props.getProperty("rpi_port");

        return "http://" + ip + ":" + port;
    }

    @NonNull
    private Properties getProperties() {
        Properties props = new Properties();
        try {
            InputStream inputStream = RetrofitClientFlightQuality.context.getAssets().open("config.properties");
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public static synchronized RetrofitClientFlightQuality getInstance(Context context) {
        RetrofitClientFlightQuality.context = context;
        if (instance == null) {
            instance = new RetrofitClientFlightQuality();
        }
        return instance;
    }

    public FlightQualityApi getFlightQualityApi() {
        return flightQualityApi;
    }
}
