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
        // sms ?????? ????????????
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
        // ???????????? layout
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

        // ?????????????????? Auth ???????????? ?????????
        mAuth = FirebaseAuth.getInstance();

        // ????????? ?????? ?????? ??? ?????? ?????????(-) ??????
        phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // ?????????, ????????????, ???????????? ??? ????????? ??????
        phoneEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(13)});
        authNumEt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        yearEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4)});
        monthEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2)});
        dayEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2)});
        pwEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(18)});

        // ????????? ?????????
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        useItem.editTextHide(this);

        // ????????? ?????? Retrofit ??????
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

        // ??????, ??????, ???????????? ??? 2?????? ?????? 8~18??? ???????????? ??????
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
                    pwCheckTv.setText("?????? ????????? ???????????? ?????????.");
                    pwCheckTv.setTextColor(Color.parseColor("#0022FF"));
                } else {
                    pwCheckTv.setText("??????, ??????, ???????????? ??? 2?????? ?????????????????? ????????????.");
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
        Toast.makeText(Activity_signUp.this, number + "??? ??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
        // ?????? ?????? ??????
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+1" + number)
                .setTimeout(90L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    // ???????????? ?????? ?????? ?????? ??????(???????????????, ?????????????????? ???) ?????? ??????
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        String message = "";
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "???????????? ?????? ?????????????????????.";
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            message = "???????????? ?????? ?????? ???????????????.";
                        }
                        showInfoDialog("?????? ?????? ?????? ??????", message, 0);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        mVerificationId = verificationId;
                        mResendToken = token;

                        if (cnd == 0) {
                            // ???????????? ?????? ????????? ?????? ?????? ?????? ?????? ??????
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
                // ?????? ???????????? ??????
                tv01.setVisibility(View.VISIBLE);
                tv02.setVisibility(View.VISIBLE);
                // ??????????????? ?????? ?????? ???????????? ?????? ?????? ?????????
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
        // ????????? ?????? ?????? ??????
        codeNum = authNumEt.getText().toString();
        if(codeNum.length() < 1) {
            // ??????????????? ???????????? ????????? ???????????? ??????????????? ??????
            Toast.makeText(this, "???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }
        // ??????????????? ???????????? ??? ???????????? ????????? verificationId ??? ???????????? ?????? ?????? ??????
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeNum);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // ????????? ???
                    API.getId(number).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            showToast(response.body());
                            if(response.isSuccessful()) {
                                if(response.body() != null) {
                                    showInfoDialog("??????", "?????? ??????????????? ????????? ????????? ????????????.\n?????????????????? ????????????.", 0);
                                    authCheckBtn.setEnabled(false);
                                    tv01.setVisibility(View.GONE);
                                    tv02.setVisibility(View.GONE);
                                    timeTv.setText("?????? ????????? ?????????????????? ????????????.");
                                    closeBtn.setVisibility(View.VISIBLE);
                                }
                                timerTask.cancel();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(Activity_signUp.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            smsAuthLayout.setVisibility(View.INVISIBLE);
                            signUpLayout.setVisibility(View.VISIBLE);
                            telTv.setText(number);
                            timerTask.cancel();
                        }
                    });
                } else {
                    // ???????????? ?????? ???
                    Toast.makeText(Activity_signUp.this, "??????????????? ???????????? ????????????.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signUp() {
        loadingDialog = ProgressDialog.show(Activity_signUp.this, "??????????????? ...", "Please Wait...", true, false);
        Map<String, String> map = new HashMap<>();
        String id = "";
        String pw = "";
        String nm = "";
        String nickName = "";
        String birth = "";
        String secAddress = "";

        if(idEt.getText().toString().isEmpty()) {
            showToast("???????????? ??????????????????.");
            return;
        } else {
            id = idEt.getText().toString();
        }

        if(pwEt.getText().toString().isEmpty()) {
            showToast("??????????????? ??????????????????.");
            return;
        } else {
            pw = pwEt.getText().toString();
        }

        if(nameEt.getText().toString().isEmpty()) {
            showToast("????????? ??????????????????.");
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
            showToast("????????? ??????????????????.");
            return;
        }

        if(yearEt.getText().toString().isEmpty()) {
            showToast("?????? ????????? ??????????????????.");
            return;
        } else if(monthEt.getText().toString().isEmpty()) {
            showToast("?????? ?????? ??????????????????");
            return;
        } else if(dayEt.getText().toString().isEmpty()) {
            showToast("?????? ?????? ??????????????????.");
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
                    showToast("????????? ????????? ??????????????????.");
                    return;
                } else if(i_month > 12) {
                    showToast("????????? ?????? ??????????????????.");
                    return;
                } else if(i_day > dayLimit) {
                    showToast("????????? ?????? ??????????????????.");
                    return;
                }
                birth = year + month + day;
                map.put("birth", birth);
            } else {
                showToast("??????????????? ????????? ???????????? ??????????????????.");
            }
        }

        if(addressFindTv.getText().toString().isEmpty()) {
            showToast("????????? ?????????????????? ????????????.");
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
                    showInfoDialog("??????", "??????????????? ??????????????????.\n????????? ???????????? ???????????????.", 1);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                loadingDialog.dismiss();
                showInfoDialog("??????", "???????????? ??????", 1);
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
                            // ????????? ??????
                            cancel();
                            // ????????? ????????? ?????? ?????? ?????? ??? ????????? ?????? ?????? ??????????????? ?????? ?????? ??????
                            tv01.setVisibility(View.GONE);
                            tv02.setVisibility(View.GONE);
                            timeTv.setText("????????? ?????????????????????. ?????? ??????????????????.");
                            moreRequestBtn.setEnabled(true);
                            authCheckBtn.setEnabled(false);
                        }
                    }
                });
            }
        };
        // 1????????? run ??????
        timer.schedule(timerTask,0,1000);
    }

    private void hideKeypad (Button btn) {
        // ????????? ?????????
        imm.hideSoftInputFromWindow(btn.getWindowToken(), 0);
    }

    // Dialog ?????????
    private void showInfoDialog(String title, String message, int condition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_signUp.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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