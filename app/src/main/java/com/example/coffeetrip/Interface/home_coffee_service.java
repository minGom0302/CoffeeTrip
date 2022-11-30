package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.DTO.DTO_image;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface home_coffee_service {
    String URL = "http://119.148.144.244:9172/";

    @GET("home/coffee/all")
    Call<List<DTO_home_coffee>> getAllDTO();

    @Multipart
    @POST("uploadFiles")
    Call<String> uploadMultipleFiles(
            @Part ArrayList<MultipartBody.Part> files,
            @Part MultipartBody.Part seq,
            @Part MultipartBody.Part uploader);

    @Multipart
    @POST("uploadFilesToTitle")
    Call<String> uploadMultipleFilesToTitle(
            @Part ArrayList<MultipartBody.Part> files,
            @Part MultipartBody.Part seq,
            @Part MultipartBody.Part uploader);
}
