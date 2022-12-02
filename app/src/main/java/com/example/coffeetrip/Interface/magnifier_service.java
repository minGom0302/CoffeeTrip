package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_magnifier;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface magnifier_service {
    @GET("magnifier/distance")
    Call<List<DTO_magnifier>> getDataDistance(@Query("nowLat") double nowLat, @Query("nowLng") double nowLng, @Query("distance") double distance);
}
