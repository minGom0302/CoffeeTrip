package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_userInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface userInfo_service {
    @GET("user/login")
    Call<DTO_userInfo> loginCheck (@Query("id") String id, @Query("pw") String pw);
}
