package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_home_coffee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface home_coffee_service {
    String URL = "http://119.148.144.244:9172/";

    @GET("home/coffee/all")
    Call<List<DTO_home_coffee>> getAllDTO();
}
