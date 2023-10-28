package com.licenta.monitorizareavioane.liveflights;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientOpensky {

    private static RetrofitClientOpensky instance = null;
    private OpenskyApi openskysApi;

    private RetrofitClientOpensky() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(OpenskyApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openskysApi = retrofit.create(OpenskyApi.class);
    }

    public static synchronized RetrofitClientOpensky getInstance() {
        if (instance == null) {
            instance = new RetrofitClientOpensky();
        }
        return instance;
    }

    public OpenskyApi getOpenskyApi() {
        return openskysApi;
    }
}