package com.example.coffeetrip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.coffeetrip.DTO.DTO_userInfo;
import com.example.coffeetrip.Interface.userInfo_service;
import com.example.coffeetrip.use.useItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Intro extends AppCompatActivity {
    SharedPreferences.Editor sp_e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 통신 시 JSON 사용과 파싱을 위한 생성
        useItem.setGsonAndRetrofit();

        getSupportActionBar().hide(); // 액션바 숨기기

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 5초 뒤 실행
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Activity_Intro.this);
                // 자동 로그인 체크 여부 확인
                boolean autoLoginCheck = sp.getBoolean("autoLoginCb", false);
                if(autoLoginCheck) {
                    // 자동 로그인 실행
                    sp_e = sp.edit();
                    autoLogin(sp.getString("id", null), sp.getString("pw", null));
                } else {
                    // 자동 로그인 실행 안함 > 로그인 화면으로 이동
                    Intent intent = new Intent(Activity_Intro.this, Activity_Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000);
    }

    private void autoLogin(String id, String pw) {
        // 자동 로그인 실행
        userInfo_service API = useItem.getRetrofit().create(userInfo_service.class);
        API.loginCheck(id, pw).enqueue(new Callback<DTO_userInfo>() {
            @Override
            public void onResponse(Call<DTO_userInfo> call, Response<DTO_userInfo> response) {
                if(response.isSuccessful()) {
                    DTO_userInfo dto = response.body();
                    // 기본 정보 내부에 저장 > 언제든 빨리 불러와 사용하기 위해서
                    sp_e.putString("pw", dto.pw);
                    sp_e.putString("nm", dto.nm);
                    sp_e.putString("nickname", dto.nickname);
                    sp_e.putString("address", dto.address);
                    sp_e.putString("phone", dto.phone);
                    sp_e.putBoolean("autoLoginCb", true);

                    sp_e.commit();

                    Intent intent = new Intent(Activity_Intro.this, Activity_Main.class);
                    startActivity(intent);
                    Toast.makeText(Activity_Intro.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DTO_userInfo> call, Throwable t) {

            }
        });
    }
}