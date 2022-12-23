package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_userInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface userInfo_service {
    @GET("user/login")
    Call<DTO_userInfo> loginCheck (@Query("id") String id, @Query("pw") String pw);

    @GET("user/id")
    Call<String> getId(
            @Query("phone") String phone);

    @GET("user/info")
    Call<DTO_userInfo> getUserInfo(
            @Query("id") String id);

    @PUT("user/pwChange")
    Call<Integer> pwChange (
            @Query("pw") String pw,
            @Query("phone") String phone);

    @Multipart
    @POST("user/signup")
    Call<Integer> signUp(
            @PartMap Map<String, String> data);

    @Multipart
    @POST("user/info/change")
    Call<Integer> myInfoChange(
            @PartMap Map<String, String> data);
}
