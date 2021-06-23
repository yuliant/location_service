package com.masrizal.android.locationservice.api;

import com.masrizal.android.locationservice.model.Respon;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("yourpost")
    Call<Respon> SendGps(
            @Field("api") String api ,
            @Field("blabla") String blabla,
            @Field("myLatitude") String myLatitude,
            @Field("myLongtitude") String myLongtitude
    );
}
