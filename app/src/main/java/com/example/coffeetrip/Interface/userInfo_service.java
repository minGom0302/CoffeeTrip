package com.example.coffeetrip.Interface;

import com.example.coffeetrip.DTO.DTO_userInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface userInfo_service {
    String URL = "http://119.148.144.244:9172/";

    @GET("user/login")
    Call<DTO_userInfo> loginCheck (@Query("id") String id, @Query("pw") String pw);
    // 서버에 접속해서 mapper 랑 service 생성부터 시작 > 로그인만들기 중이였음
}
