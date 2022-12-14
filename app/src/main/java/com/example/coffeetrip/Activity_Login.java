package com.example.coffeetrip;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.coffeetrip.DTO.DTO_userInfo;
import com.example.coffeetrip.Interface.userInfo_service;
import com.example.coffeetrip.handler.backspaceHandler;
import com.example.coffeetrip.use.useItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Login extends AppCompatActivity {
    Button signUpBtn, loginBtn, pwFindBtn, uploadPageBtn;
    EditText idEt, pwEt;
    CheckBox saveIdCb, autoLoginCb;
    LinearLayout linearLayout;

    backspaceHandler bsHandler = new backspaceHandler(this);
    useItem item = new useItem();
    DTO_userInfo dto = null;

    userInfo_service userAPI;
    SharedPreferences sp;
    SharedPreferences.Editor sp_e;
    ProgressDialog loadingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 핸드폰 내부에 저장하기 위해 객체 생성
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp_e = sp.edit();

        // API 설정
        userAPI = useItem.getRetrofit().create(userInfo_service.class);

        linearLayout = (LinearLayout) findViewById(R.id.activity_login_layout);
        idEt = (EditText) findViewById(R.id.login_idEt);
        pwEt = (EditText) findViewById(R.id.login_pwEt);
        loginBtn = (Button) findViewById(R.id.login_loginBtn);
        signUpBtn = (Button) findViewById(R.id.login_singUpBtn);
        pwFindBtn = (Button) findViewById(R.id.login_pwFindBtn);
        uploadPageBtn = (Button) findViewById(R.id.uploadPageBtn);
        saveIdCb = (CheckBox) findViewById(R.id.login_saveId);
        autoLoginCb = (CheckBox) findViewById(R.id.login_autoLogin);

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                useItem.editTextHide(Activity_Login.this);
                return false;
            }
        });

        loginBtn.setOnClickListener(v -> {
            String id = idEt.getText().toString();
            String pw = pwEt.getText().toString();

            // 아이디 비번 입력 여부 확인
            if(id.getBytes().length < 1 || pw.getBytes().length < 1) {
                item.toastMsg(this, "아이디 혹은 비밀번호를 입력해주세요.");
            } else {
                loadingDialog = ProgressDialog.show(Activity_Login.this, "로그인 중 ...", "Please Wait...", true, false);
                loginCheck(id, pw);
            }
        });
        signUpBtn.setOnClickListener(v -> {
            changeView(0);
        });
        pwFindBtn.setOnClickListener(v -> {
            changeView(1);
        });
        uploadPageBtn.setOnClickListener(v -> {
            changeView(3);
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
        } else if(num == 3) {
            intent = new Intent(this, Activity_UploadPage.class);
        }

        startActivity(intent);
    }

    // DB 정보 확인 (가입 여부 확인)
    private void loginCheck(String id, String pw) {
        userAPI.loginCheck(id, pw).enqueue(new Callback<DTO_userInfo>() {
            @Override
            public void onResponse(Call<DTO_userInfo> call, Response<DTO_userInfo> response) {
                if(response.isSuccessful()) {
                    dto = response.body();
                    implementLogin();
                } else {
                    loadingDialog.dismiss();
                    item.toastMsg(getApplicationContext(), "아이디 혹은 비밀번호를 확인하세요.");
                }
            }

            @Override
            public void onFailure(Call<DTO_userInfo> call, Throwable t) {
                loadingDialog.dismiss();
                item.toastMsg(getApplicationContext(), "아이디 혹은 비밀번호를 확인하세요.");
            }
        });
    }

    // 로그인 실행
    private void implementLogin() {
        if(dto == null) {
            // return 된 자료가 없으면 로그인 실패
            item.toastMsg(this, "아이디 혹은 비밀번호를 확인하세요.");
        } else {
            // 기본 정보 내부에 저장 > 언제든 빨리 불러와 사용하기 위해서
            sp_e.putString("pw", dto.pw);
            sp_e.putString("nm", dto.nm);
            sp_e.putString("nickname", dto.nickname);
            sp_e.putString("address", dto.address);
            sp_e.putString("phone", dto.phone);
            // 아이디 저장 체크박스 확인하여 설정
            if(saveIdCb.isChecked()) {
                sp_e.putString("id", dto.id);
                sp_e.putBoolean("saveId", true);
            } else if(!saveIdCb.isChecked()){
                sp_e.putBoolean("saveId", false);
            }

            if(autoLoginCb.isChecked()) {
                sp_e.putBoolean("autoLoginCb", true);
            } else if(!autoLoginCb.isChecked()){
                sp_e.putBoolean("autoLoginCb", false);
            }

            sp_e.commit();
            loadingDialog.dismiss();
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
        boolean saveId = sp.getBoolean("saveId", false);
        boolean autoLoginCheck = sp.getBoolean("autoLoginCb", false);
        if(saveId) {
            idEt.setText(id);
            saveIdCb.setChecked(true);
        }
        if(autoLoginCheck) {
            autoLoginCb.setChecked(true);
        }
    }

   @Override
    public void onBackPressed() { bsHandler.onBackPressed(); }
}