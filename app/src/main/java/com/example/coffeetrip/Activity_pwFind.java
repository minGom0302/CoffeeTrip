package com.example.coffeetrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeetrip.Interface.userInfo_service;
import com.example.coffeetrip.handler.backspaceHandler;
import com.example.coffeetrip.use.useItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_pwFind extends AppCompatActivity {
    LinearLayout smsLayout, changePwLayout, numInputLayout;
    TextView title, timeTv, tv01, tv02, idTv;
    EditText phoneEt, authNumEt, newPwEt, oneMorePwEt;
    Button requestBtn, authCheckBtn, changePwBtn, moreRequestBtn;

    Timer timer = new Timer();;
    TimerTask timerTask;
    InputMethodManager imm;
    ProgressDialog loadingDialog;
    userInfo_service API;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private backspaceHandler bsHandler = new backspaceHandler(this);

    String number;
    String mVerificationId;
    String codeNum;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_find);

        // 파이어베이스 Auth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        smsLayout = (LinearLayout) findViewById(R.id.pwFind_smsAuth_layout);
        changePwLayout = (LinearLayout) findViewById(R.id.pwFind_changePw_layout);
        numInputLayout = (LinearLayout) findViewById(R.id.pwFind_numInputLayout);
        title = (TextView) findViewById(R.id.pwFind_title);
        timeTv = (TextView) findViewById(R.id.pwFind_timeTv);
        tv01 = (TextView) findViewById(R.id.pwFind_Tv01);
        tv02 = (TextView) findViewById(R.id.pwFind_Tv02);
        idTv = (TextView) findViewById(R.id.pwFind_yourIdTv);
        phoneEt = (EditText) findViewById(R.id.pwFind_phoneEt);
        authNumEt = (EditText) findViewById(R.id.pwFind_authNumEt);
        newPwEt = (EditText) findViewById(R.id.pwFind_newPwEt);
        oneMorePwEt = (EditText) findViewById(R.id.pwFind_oneMorePwEt);
        requestBtn = (Button) findViewById(R.id.pwFind_requestBtn);
        authCheckBtn = (Button) findViewById(R.id.pwFind_authCheckBtn);
        changePwBtn = (Button) findViewById(R.id.pwFind_changePwBtn);
        moreRequestBtn = (Button) findViewById(R.id.pwFind_moreRequestBtn);

        // 핸드폰 번호 입력 시 자동 하이픈(-) 추가
        phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 핸드폰, 인증번호, 비밀번호 자릿수 제한
        phoneEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(13)});
        authNumEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        newPwEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});
        oneMorePwEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});

        // 키패드 컨트롤
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        useItem.editTextHide(Activity_pwFind.this);

        // 통신을 위한 Retrofit 설정
        API = useItem.getRetrofit().create(userInfo_service.class);

        requestBtn.setOnClickListener(v -> {
            number = phoneEt.getText().toString();
            hideKeypad(requestBtn);
            requestAuthNum(0);
        });

        authCheckBtn.setOnClickListener(v -> {
            hideKeypad(authCheckBtn);
            authCheck();
        });

        changePwBtn.setOnClickListener(v -> {
            hideKeypad(changePwBtn);
            changePw();
        });

        moreRequestBtn.setOnClickListener(v -> {
            hideKeypad(moreRequestBtn);
            requestAuthNum(1);
        });

    }

    private void requestAuthNum(int cnd) {
        Toast.makeText(Activity_pwFind.this, number + "로 인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
        // 인증 코드 요청
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+1" + number)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) { }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        String message = "";
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "유효하지 않은 전화번호입니다.";
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            message = "유효하지 않은 인증 요청입니다.";
                        }
                        showInfoDialog("인증 코드 요청 실패", message, 0);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        mVerificationId = verificationId;
                        mResendToken = token;

                        if (cnd == 0) {
                            // 인증번호 입력 보이게 하고 인증 요청 버튼 막기
                            numInputLayout.setVisibility(View.VISIBLE);
                            requestBtn.setEnabled(false);
                            timerTask();
                        } else if (cnd == 1) {
                            // 다시 본인인증 요청
                            tv01.setVisibility(View.VISIBLE);
                            tv02.setVisibility(View.VISIBLE);
                            // 재전송버튼 다시 막고 인증번호 확인 버튼 살리기
                            moreRequestBtn.setEnabled(false);
                            authCheckBtn.setEnabled(true);
                            timerTask();
                        }
                    }
                })
                .build();
        if (mResendToken != null) {
            PhoneAuthOptions.newBuilder(mAuth).setForceResendingToken(mResendToken);
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }

    private void authCheck() {
        // ProgressDialog
        // 입력한 코드 번호 저장
        codeNum = authNumEt.getText().toString();
        if(codeNum.length() < 1) {
            // 인증번호를 입력안한 상태를 체크하여 입력하게끔 유도
            Toast.makeText(this, "인증코드 번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        // 코드번호와 요청했을 때 응답으로 들어온 verificationId 를 이용하여 일치 여부 확인
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeNum);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // 일치할 때
                    API.getId(number).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()) {
                                if(response.body() != null) {
                                    Toast.makeText(Activity_pwFind.this, "본인인증이 확인되었습니다.", Toast.LENGTH_SHORT).show();
                                    String id = response.body();
                                    int length = id.length();
                                    String secretId = id.substring(0, length-4) + "****";
                                    idTv.setText(secretId);
                                    title.setText("비밀번호 변경");
                                    smsLayout.setVisibility(View.INVISIBLE);
                                    changePwLayout.setVisibility(View.VISIBLE);
                                    timerTask.cancel();
                                } else {
                                    showInfoDialog("경고", "가입된 회원 정보가 없습니다.\n다시 확인해주시기 바랍니다.", 1);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });

                } else {
                    // 일치하지 않을 때
                    Toast.makeText(Activity_pwFind.this, "인증코드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void changePw() {
        String newPw = newPwEt.getText().toString();
        String oneMorePw = oneMorePwEt.getText().toString();

        if(newPw.length() > 7 && newPw.equals(oneMorePw)) {
            loadingDialog = ProgressDialog.show(Activity_pwFind.this, "비밀번호 변경중 ...", "Please Wait...", true, false);
            API.pwChange(oneMorePw,number).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.isSuccessful()) {
                        // update, delete, put 은 integer 로 리턴값을 받아야 성공 실패 여부를 알 수 있다.
                        // 성공 시 적용된 행의 갯수가 리턴됨
                        loadingDialog.dismiss();
                        showInfoDialog("비밀번호 변경 완료", "비밀번호가 변경되었습니다. \n다시 로그인을 시도해주세요.", 1);
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    loadingDialog.dismiss();
                }
            });
        } else if (newPw.length() < 8) {
            showInfoDialog("비밀번호 변경 실패", "비밀번호가 8자리 이상이 아닙니다.", 0);
        } else {
            showInfoDialog("비밀번호 변경 실패", "비밀번호가 일치하지 않습니다.", 0);
        }
    }

    private void timerTask() {
        timerTask = new TimerTask() {
            int time = 120;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(time != 0) {
                            timeTv.setText(String.valueOf(time));
                            time -= 1;
                        } else {
                            // 타이머 취소
                            cancel();
                            // 텍스트 가리고 시간 초과 표시 및 재전송 버튼 열고 인증번번호 확인 버튼 막기
                            tv01.setVisibility(View.GONE);
                            tv02.setVisibility(View.GONE);
                            timeTv.setText("시간이 초과되었습니다. 다시 시도해주세요.");
                            moreRequestBtn.setEnabled(true);
                            authCheckBtn.setEnabled(false);
                        }
                    }
                });
            }
        };
        // 1초마다 run 실행
        timer.schedule(timerTask,0,1000);
    }

    private void hideKeypad (Button btn) {
        // 키보드 내리기
        imm.hideSoftInputFromWindow(btn.getWindowToken(), 0);
    }

    // Dialog 띄우기
    private void showInfoDialog(String title, String message, int condition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_pwFind.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(condition == 1) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        bsHandler.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }
}