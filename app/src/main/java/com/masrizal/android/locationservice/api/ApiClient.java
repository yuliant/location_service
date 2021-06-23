package com.masrizal.android.locationservice.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String DATA_URL = "https://yourdomain.com/";
    private static Retrofit retrofit;
    public static Retrofit getData(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(DATA_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
