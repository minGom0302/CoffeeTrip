package com.example.coffeetrip;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coffeetrip.DTO.DTO_userInfo;
import com.example.coffeetrip.Interface.userInfo_service;
import com.example.coffeetrip.handler.backspaceHandler;
import com.example.coffeetrip.use.useItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Login extends AppCompatActivity {
    Button signUpBtn, loginBtn, pwFindBtn;
    EditText idEt, pwEt;
    CheckBox saveIdCb, autoLoginCb;

    backspaceHandler bsHandler = new backspaceHandler(this);
    useItem item = new useItem();
    DTO_userInfo dto = null;

    Retrofit retrofit;
    Gson gson;
    userInfo_service userAPI;
    SharedPreferences sp;
    SharedPreferences.Editor sp_e;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 핸드폰 내부에 저장하기 위해 객체 생성
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp_e = sp.edit();

        // 통신 시 JSON 사용과 파싱을 위한 생성
        gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(userInfo_service.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // API 설정
        userAPI = retrofit.create(userInfo_service.class);

        idEt = (EditText) findViewById(R.id.login_idEt);
        pwEt = (EditText) findViewById(R.id.login_pwEt);
        loginBtn = (Button) findViewById(R.id.login_loginBtn);
        signUpBtn = (Button) findViewById(R.id.login_singUpBtn);
        pwFindBtn = (Button) findViewById(R.id.login_pwFindBtn);
        saveIdCb = (CheckBox) findViewById(R.id.login_saveId);
        autoLoginCb = (CheckBox) findViewById(R.id.login_autoLogin);

        loginBtn.setOnClickListener(v -> {
            String id = idEt.getText().toString();
            String pw = pwEt.getText().toString();

            // 아이디 비번 입력 여부 확인
            if(id.getBytes().length < 1 || pw.getBytes().length < 1) {
                item.toastMsg(this, "아이디 혹은 비밀번호를 입력해주세요.");
            } else {
                // DB 정보 확인
                new Thread(() -> {
                    loginCheck(id, pw);
                }).start();

                // 로그인 실행
                implementLogin();
            }
        });
        signUpBtn.setOnClickListener(v -> {
            changeView(0);
        });
        pwFindBtn.setOnClickListener(v -> {
            changeView(1);
        });

        // 아이디 저장 여부 확인
        checkBoxSetUp();
    }

    // 화면 변경 메소드
    private void changeView(int num) {
        Intent intent = null;

        if(num == 0) {
            intent = new Intent(this, Activity_signUp.class);
        } else if(num == 1) {
            intent = new Intent(this, Activity_pwFind.class);
        } else if(num == 2) {
            intent = new Intent(this, Activity_Main.class);
        }

        startActivity(intent);
    }

    // DB 정보 확인 (가입 여부 확인)
    private void loginCheck(String id, String pw) {
        try {
            dto = userAPI.loginCheck(id, pw).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 로그인 실행
    private void implementLogin() {
        if(dto == null) {
            // return 된 자료가 없으면 로그인 실패
            item.toastMsg(this, "아이디 혹은 비밀번호를 확인하세요.");
        } else {
            // 기본 정보 내부에 저장 > 언제든 빨리 불러와 사용하기 위해서
            sp_e.putString("nm", dto.nm);
            sp_e.putString("nickname", dto.nickname);
            sp_e.putString("address", dto.address);
            sp_e.putString("phone", dto.phone);
            // 아이디 저장 체크박스 확인하여 설정
            if(saveIdCb.isChecked()) {
                sp_e.putString("id", dto.id);
                sp_e.putBoolean("saveId", true);
            } else {
                sp_e.putBoolean("saveId", false);
            }
            sp_e.commit();

            // 메인 화면으로
            changeView(2);
            // 로그인 성공 멘트
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
            // 로그인 액티비티 종료
            finish();
        }
    }

    private void checkBoxSetUp() {
        String id = sp.getString("id", null);
        Boolean saveId = sp.getBoolean("saveId", false);
        if(saveId) {
            idEt.setText(id);
            saveIdCb.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() { bsHandler.onBackPressed(); }
}