package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_magnifier;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface magnifier_service {
    String URL = "http://119.148.144.244:9172/";

    @GET("magnifier/all")
    Call<List<DTO_magnifier>> getAddressDTO();
}
