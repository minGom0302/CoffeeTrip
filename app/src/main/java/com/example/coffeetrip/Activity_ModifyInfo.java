package com.example.coffeetrip;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeetrip.DTO.DTO_userInfo;
import com.example.coffeetrip.Interface.userInfo_service;
import com.example.coffeetrip.handler.backspaceHandler;
import com.example.coffeetrip.use.useItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_ModifyInfo extends AppCompatActivity {
    TextView pwCheckTv, telTv, addressFindTv;
    EditText idEt, pwEt, nameEt, yearEt, monthEt, dayEt, nickNameEt, addressInputEt;
    RadioGroup radioGroup;
    RadioButton rb01, rb02;
    Button modifyBtn;

    private final backspaceHandler bsHandler = new backspaceHandler(this);
    userInfo_service API = useItem.getRetrofit().create(userInfo_service.class);
    ProgressDialog loadingDialog;
    SharedPreferences sp;
    SharedPreferences.Editor sp_e;

    private static final int ADDRESS_WEB_VIEW_ACTIVITY = 100000;
    String id = "";
    String nick = "";
    String year = "";
    String month = "";
    String day = "";
    String sex = "";
    String name = "";
    String pw = "";
    String tel = "";
    String address = "";
    String secAddress = "";
    String birth = "";
    String originalNick = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);

        sp = PreferenceManager.getDefaultSharedPreferences(Activity_ModifyInfo.this);
        sp_e = sp.edit();

        loadingDialog = ProgressDialog.show(Activity_ModifyInfo.this, "가져오는 중", "Please wait....", true, false);

        pwCheckTv = findViewById(R.id.modify_pwCheckTv);
        telTv = findViewById(R.id.modify_telTv);
        addressFindTv = findViewById(R.id.modify_addressFindTv);
        idEt = findViewById(R.id.modify_idEt);
        pwEt = findViewById(R.id.modify_pwEt);
        nameEt = findViewById(R.id.modify_nameEt);
        yearEt = findViewById(R.id.modify_yearEt);
        monthEt = findViewById(R.id.modify_monthEt);
        dayEt = findViewById(R.id.modify_dayEt);
        nickNameEt = findViewById(R.id.modify_nickNameEt);
        addressInputEt = findViewById(R.id.modify_addressInputEt);
        radioGroup = findViewById(R.id.modify_radioGroup);
        rb01 = findViewById(R.id.modify_rb01);
        rb02 = findViewById(R.id.modify_rb02);
        modifyBtn = findViewById(R.id.modify_modifyBtn);

        idEt.setEnabled(false);

        yearEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4)});
        monthEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2)});
        dayEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2)});
        pwEt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(18)});

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

        addressFindTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_ModifyInfo.this, Activity_webView.class);
                startActivityForResult(intent, ADDRESS_WEB_VIEW_ACTIVITY);
            }
        });

        modifyBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("안내").setMessage("입력한 정보로 변경하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateMyInfo();
                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        setLayout();
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

    private void setLayout() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        API.getUserInfo(id).enqueue(new Callback<DTO_userInfo>() {
            @Override
            public void onResponse(Call<DTO_userInfo> call, Response<DTO_userInfo> response) {
                if(response.isSuccessful()) {
                    DTO_userInfo dto = response.body();

                    name = dto.getNm();
                    pw = dto.getPw();
                    tel = dto.getPhone();
                    address = dto.getAddress();
                    secAddress = dto.getSecAddress();
                    Log.i("TAG,,,,", dto.getBirth());
                    year = dto.getBirth().substring(0, 4);
                    month = dto.getBirth().substring(4, 6);
                    day = dto.getBirth().substring(6, 8);
                    nick = dto.getNickname();
                    originalNick = nick;
                    sex = dto.getSex();

                    idEt.setText(dto.getId());
                    pwEt.setText(dto.getPw());
                    nameEt.setText(dto.getNm());
                    yearEt.setText(year);
                    monthEt.setText(month);
                    dayEt.setText(day);
                    if(sex.equals("0")) {
                        rb01.setChecked(true);
                        rb02.setChecked(false);
                    } else if(sex.equals("1")) {
                        rb01.setChecked(false);
                        rb02.setChecked(true);
                    }
                    nickNameEt.setText(nick);
                    telTv.setText(dto.getPhone());
                    addressFindTv.setText(dto.getAddress());
                    addressInputEt.setText(dto.getSecAddress());
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DTO_userInfo> call, Throwable t) {
                loadingDialog.dismiss();
            }
        });
    }

    private void updateMyInfo() {
        Map<String, String> map = new HashMap<>();

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
            name = nameEt.getText().toString();
        }

        if(nickNameEt.getText().toString().isEmpty()) {
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            nick = "user" + random.nextInt(9999) + random.nextInt(9999);
        } else {
            nick = nickNameEt.getText().toString();
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
        map.put("name", name);
        map.put("nickname", nick);
        map.put("tel", tel);
        map.put("sex", sex);
        map.put("secAddress", secAddress);
        map.put("originalNick", originalNick);

        API.myInfoChange(map).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    showDialog("안내", "정보 변경이 완료되었습니다.", 0);
                } else {
                    showDialog("안내", "정보 변경에 실패했습니다.\n다시 시도해주시기 바랍니다.", 1);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                showDialog("안내", "정보 변경에 실패했습니다.\n다시 시도해주시기 바랍니다.", 1);
            }
        });
    }

    private void showDialog(String title, String msg, int cnd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ModifyInfo.this);
        builder.setTitle(title).setMessage(msg);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(cnd == 0) {
                    sp_e.putString("nickname", nick);
                    sp_e.commit();
                    Bundle extra = new Bundle();
                    Intent intent = new Intent();
                    extra.putString("nick", nick);
                    intent.putExtras(extra);
                    setResult(RESULT_OK, intent);
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
}