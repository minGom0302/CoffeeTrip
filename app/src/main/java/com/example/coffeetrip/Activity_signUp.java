package com.example.coffeetrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeetrip.Interface.userInfo_service;
import com.example.coffeetrip.handler.backspaceHandler;
import com.example.coffeetrip.use.useItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_signUp extends AppCompatActivity {
    LinearLayout smsAuthLayout, numInputLayout, signUpLayout;
    TextView tv01, tv02, timeTv, telTv, pwCheckTv, addressFindTv;
    EditText phoneEt, authNumEt, idEt, pwEt, nameEt, nickNameEt, yearEt, monthEt, dayEt, addressInputEt;
    Button requestBtn, moreRequestBtn, authCheckBtn, signUpBtn, closeBtn;
    RadioButton rb01, rb02;
    RadioGroup radioGroup;

    Timer timer = new Timer();
    TimerTask timerTask;
    InputMethodManager imm;
    ProgressDialog loadingDialog;
    userInfo_service API;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private final backspaceHandler bsHandler = new backspaceHandler(this);

    private static final int ADDRESS_WEB_VIEW_ACTIVITY = 10000;
    String number, mVerificationId, codeNum, sex;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // sms 인증 레이아웃
        smsAuthLayout = findViewById(R.id.signUp_smsAuth_layout);
        numInputLayout = findViewById(R.id.signUp_numInputLayout);
        signUpLayout = findViewById(R.id.signUp_signUpLayout);
        tv01 = findViewById(R.id.signUp_Tv01);
        tv02 = findViewById(R.id.signUp_Tv02);
        timeTv = findViewById(R.id.signUp_timeTv);
        phoneEt = findViewById(R.id.signUp_phoneEt);
        authNumEt = findViewById(R.id.signUp_authNumEt);
        requestBtn = findViewById(R.id.signUp_requestBtn);
        moreRequestBtn = findViewById(R.id.signUp_moreRequestBtn);
        authCheckBtn = findViewById(R.id.signUp_authCheckBtn);
        closeBtn = findViewById(R.id.signUp_closeBtn);
        // 회원가입 layout
        telTv = findViewById(R.id.signUp_telTv);
        pwCheckTv = findViewById(R.id.signUp_pwCheckTv);
        addressFindTv = findViewById(R.id.signUp_addressFindTv);
        idEt = findViewById(R.id.signUp_idEt);
        pwEt = findViewById(R.id.signUp_pwEt);
        nameEt = findViewById(R.id.signUp_nameEt);
        nickNameEt = findViewById(R.id.signUp_nickNameEt);
        yearEt = findViewById(R.id.signUp_yearEt);
        monthEt = findViewById(R.id.signUp_monthEt);
        dayEt = findViewById(R.id.signUp_dayEt);
        addressInputEt = findViewById(R.id.signUp_addressInputEt);
        rb01 = findViewById(R.id.signUp_rb01);
        rb02 = findViewById(R.id.signUp_rb02);
        radioGroup = findViewById(R.id.signUp_radioGroup);
        signUpBtn = findViewById(R.id.signUp_signUpBtn);

        // 파이어베이스 Auth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        // 핸드폰 번호 입력 시 자동 하이픈(-) 추가
        phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 핸드폰, 인증번호, 비밀번호 등 자릿수 제한
        phoneEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(13)});
        authNumEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        yearEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4)});
        monthEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2)});
        dayEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2)});
        pwEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(18)});

        // 키패드 컨트롤
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        useItem.editTextHide(this);

        // 통신을 위한 Retrofit 설정
        API = useItem.getRetrofit().create(userInfo_service.class);

        requestBtn.setOnClickListener(v -> {
            number = phoneEt.getText().toString();
            hideKeypad(requestBtn);
            requestAuthNum(0);
        });
        moreRequestBtn.setOnClickListener(v -> {
            hideKeypad(moreRequestBtn);
            requestAuthNum(1);
        });
        authCheckBtn.setOnClickListener(v -> {
            hideKeypad(authCheckBtn);
            authCheck();
        });
        closeBtn.setOnClickListener(v -> {
            hideKeypad(closeBtn);
            finish();
        });
        signUpBtn.setOnClickListener(v -> {
            hideKeypad(signUpBtn);
            signUp();
        });

        addressFindTv.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_signUp.this, Activity_webView.class);
            startActivityForResult(intent, ADDRESS_WEB_VIEW_ACTIVITY);
        });

        // 숫자, 문자, 특수문자 중 2가지 포함 8~18자 비밀번호 검사
        String pwRegex = "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).{8,18}$";
        Pattern passPattern = Pattern.compile(pwRegex);
        pwEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher passMatcher = passPattern.matcher(pwEt.getText().toString());
                if(passMatcher.find()) {
                    pwCheckTv.setText("사용 가능한 패스워드 입니다.");
                    pwCheckTv.setTextColor(Color.parseColor("#0022FF"));
                } else {
                    pwCheckTv.setText("영문, 숫자, 특수문자 중 2개를 사용해주시기 바랍니다.");
                    pwCheckTv.setTextColor(Color.parseColor("#FF0000"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.signUp_rb01) {
                    sex = "0";
                } else if(checkedId == R.id.signUp_rb02) {
                    sex = "1";
                }
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == ADDRESS_WEB_VIEW_ACTIVITY) {
            if(resultCode == RESULT_OK) {
                String data = intent.getExtras().getString("data");
                if(data != null) {
                    addressFindTv.setText(data);
                }
            }
        }
    }

    private void requestAuthNum(int cnd) {
        Toast.makeText(Activity_signUp.this, number + "로 인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
        // 인증 코드 요청
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+1" + number)
                .setTimeout(90L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    // 번호인증 혹은 기타 다른 인증(구글로그인, 이메일로그인 등) 끝난 상태
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {    }

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
                        }
                    }
                })
                .build();
        if (mResendToken != null) {
            PhoneAuthOptions.newBuilder(mAuth).setForceResendingToken(mResendToken);
            if (cnd == 1) {
                // 다시 본인인증 요청
                tv01.setVisibility(View.VISIBLE);
                tv02.setVisibility(View.VISIBLE);
                // 재전송버튼 다시 막고 인증번호 확인 버튼 살리기
                moreRequestBtn.setEnabled(false);
                authCheckBtn.setEnabled(true);
                timerTask();
            }
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
                            showToast(response.body());
                            if(response.isSuccessful()) {
                                if(response.body() != null) {
                                    showInfoDialog("경고", "해당 핸드폰으로 가입한 이력이 있습니다.\n확인해주시기 바랍니다.", 0);
                                    authCheckBtn.setEnabled(false);
                                    tv01.setVisibility(View.GONE);
                                    tv02.setVisibility(View.GONE);
                                    timeTv.setText("가입 이력을 확인해주시기 바랍니다.");
                                    closeBtn.setVisibility(View.VISIBLE);
                                }
                                timerTask.cancel();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(Activity_signUp.this, "본인인증이 확인되었습니다.", Toast.LENGTH_SHORT).show();
                            smsAuthLayout.setVisibility(View.INVISIBLE);
                            signUpLayout.setVisibility(View.VISIBLE);
                            telTv.setText(number);
                            timerTask.cancel();
                        }
                    });
                } else {
                    // 일치하지 않을 때
                    Toast.makeText(Activity_signUp.this, "인증코드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signUp() {
        loadingDialog = ProgressDialog.show(Activity_signUp.this, "회원가입중 ...", "Please Wait...", true, false);
        Map<String, String> map = new HashMap<>();
        String id = "";
        String pw = "";
        String nm = "";
        String nickName = "";
        String birth = "";
        String secAddress = "";

        if(idEt.getText().toString().isEmpty()) {
            showToast("아이디를 입력해주세요.");
            return;
        } else {
            id = idEt.getText().toString();
        }

        if(pwEt.getText().toString().isEmpty()) {
            showToast("비밀번호를 입력해주세요.");
            return;
        } else {
            pw = pwEt.getText().toString();
        }

        if(nameEt.getText().toString().isEmpty()) {
            showToast("이름을 입력해주세요.");
            return;
        } else {
            nm = nameEt.getText().toString();
        }

        if(nickNameEt.getText().toString().isEmpty()) {
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            nickName = "user" + random.nextInt(9999) + random.nextInt(9999);
        } else {
            nickName = nickNameEt.getText().toString();
        }

        if (sex == null) {
            showToast("성별을 선택해주세요.");
            return;
        }

        if(yearEt.getText().toString().isEmpty()) {
            showToast("해당 년도를 작성해주세요.");
            return;
        } else if(monthEt.getText().toString().isEmpty()) {
            showToast("해당 월을 작성해주세요");
            return;
        } else if(dayEt.getText().toString().isEmpty()) {
            showToast("해당 일을 작성해주세요.");
            return;
        } else {
            String year = yearEt.getText().toString();
            String month = monthEt.getText().toString();
            String day =  dayEt.getText().toString();

            int i_year = Integer.parseInt(year);
            int i_month = Integer.parseInt(month);
            int i_day = Integer.parseInt(day);

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.KOREA);
            int i_now_year = Integer.parseInt(sdf.format(date));
            int dayLimit;
            if(i_month == 2) {
                dayLimit = 28;
            } else if(i_month < 7) {
                if(i_month % 2 == 1) {
                    dayLimit = 31;
                } else {
                    dayLimit = 30;
                }
            } else {
                if(i_month % 2 == 1) {
                    dayLimit = 30;
                } else {
                    dayLimit = 31;
                }
            }

            if(year.length() == 4 && month.length() == 2 && day.length() == 2) {
                if(i_year > i_now_year) {
                    showToast("올바른 년도를 작성해주세요.");
                    return;
                } else if(i_month > 12) {
                    showToast("올바른 월을 작성해주세요.");
                    return;
                } else if(i_day > dayLimit) {
                    showToast("올바른 일을 작성해주세요.");
                    return;
                }
                birth = year + month + day;
                map.put("birth", birth);
            } else {
                showToast("생년월일을 올바른 형식으로 작성해주세요.");
            }
        }

        if(addressFindTv.getText().toString().isEmpty()) {
            showToast("주소를 작성해주시기 바랍니다.");
            return;
        } else {
            String add1 = addressFindTv.getText().toString();
            map.put("address", add1);
        }

        if(!addressInputEt.getText().toString().isEmpty()) {
            secAddress = addressInputEt.getText().toString();
        }

        map.put("id", id);
        map.put("pw", pw);
        map.put("name", nm);
        map.put("nickname", nickName);
        map.put("tel", number);
        map.put("sex", sex);
        map.put("secAddress", secAddress);

        API.signUp(map).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    loadingDialog.dismiss();
                    showInfoDialog("알림", "회원가입을 축하드립니다.\n로그인 화면으로 이동합니다.", 1);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                loadingDialog.dismiss();
                showInfoDialog("경고", "회원가입 실패", 1);
            }
        });
    }

    private void timerTask() {
        timerTask = new TimerTask() {
            int time = 90;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_signUp.this);
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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