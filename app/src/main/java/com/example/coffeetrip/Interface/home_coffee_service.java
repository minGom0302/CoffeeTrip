package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_home_coffee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface home_coffee_service {
    @GET("home/coffee/all")
    Call<List<DTO_home_coffee>> getAllDTO();

    @Multipart
    @POST("uploadFiles")
    Call<String> uploadMultipleFiles(
            @Part ArrayList<MultipartBody.Part> files,
            @PartMap Map<String, String> data);

    @GET("home/coffee/top")
    Call<List<DTO_home_coffee>> getTopTenDTO();

    @GET("home/coffee/recently")
    Call<List<DTO_home_coffee>> getRecentlyCafeDTO();
}
