package com.licenta.monitorizareavioane.planepics;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientPlanespotter {
    private static RetrofitClientPlanespotter instance = null;
    private PlanespotterApi planespotterApi;

    private RetrofitClientPlanespotter() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("User-Agent", "AppLicenta")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(PlanespotterApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        planespotterApi = retrofit.create(PlanespotterApi.class);
    }

    public static synchronized RetrofitClientPlanespotter getInstance() {
        if (instance == null) {
            instance = new RetrofitClientPlanespotter();
        }
        return instance;
    }

    public PlanespotterApi getPlanespotterApi() {
        return planespotterApi;
    }
}
