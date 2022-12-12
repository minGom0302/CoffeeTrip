package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_detail_menu;
import com.example.coffeetrip.DTO.DTO_detail_review;
import com.example.coffeetrip.DTO.DTO_home_coffee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface home_coffee_service {
    @GET("home/coffee/all")
    Call<List<DTO_home_coffee>> getAllDTO();

    @GET("home/coffee/top")
    Call<List<DTO_home_coffee>> getTopTenDTO();

    @GET("home/coffee/recently")
    Call<List<DTO_home_coffee>> getRecentlyCafeDTO();

    @GET("detail")
    Call<DTO_home_coffee> getDetailInfo(
            @Query("seq") int seq,
            @Query("id") String id);

    @GET("detail/menu")
    Call<List<DTO_detail_menu>> getDetailMenu(
            @Query("seq") int seq);

    @GET("detail/image")
    Call<List<DTO_detail_review>> getDetailImage(
            @Query("seq") int seq,
            @Query("type") int type);

    @GET("detail/review")
    Call<List<DTO_detail_review>> getDetailReview(
            @Query("seq") int seq );

    @PUT("cafe/plus")
    Call<Void> plusFavorite(
            @Query("seq") int seq,
            @Query("id") String id);

    @PUT("cafe/minus")
    Call<Void> minusFavorite(
            @Query("seq") int seq,
            @Query("id") String id);

    @Multipart
    @POST("uploadFiles")
    Call<String> uploadMultipleFiles(
            @Part ArrayList<MultipartBody.Part> files,
            @PartMap Map<String, String> data);

    @Multipart
    @POST("uploadFiles/review")
    Call<String> uploadReviewFiles(
            @Part ArrayList<MultipartBody.Part> files,
            @PartMap Map<String, String> data);

    @Multipart
    @POST("uploadFiles/menu")
    Call<String> uploadMenuFiles(
            @Part ArrayList<MultipartBody.Part> files,
            @PartMap Map<String, String> data);
}
