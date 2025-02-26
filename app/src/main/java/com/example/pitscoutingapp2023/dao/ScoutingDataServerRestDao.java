package com.example.pitscoutingapp2023.dao;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScoutingDataServerRestDao {

    public static Retrofit retrofit;
    public static ScoutingDataServerAPIService getApiInterface(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ScoutingDataServerAPIService.class);
    }


}
