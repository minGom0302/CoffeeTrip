package com.example.coffeetrip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeetrip.Adapter.adapter_multi_image;
import com.example.coffeetrip.Adapter.adapter_spinner;
import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_UploadPage extends AppCompatActivity {
    // 이미지의 uri를 담을 ArrayList 객체
    ArrayList<Uri> uriList = new ArrayList<>();
    // MultipartBody.Part를 담을 ArrayList 객체
    ArrayList<MultipartBody.Part> bodyList = new ArrayList<>();
    // 이미지를 보여줄 리사이클러뷰
    RecyclerView recyclerView;
    // 리사이클러뷰에 적용시킬 어댑터
    adapter_multi_image adapter;
    home_coffee_service coffeeAPI;
    // Spinner 사용을 위한 아답터
    adapter_spinner adapterSpinner;
    Spinner spinner;
    // sipnner에서 선택한 값을 저장할 변수
    String selectedItem;
    int selectedSeq;
    // 이름과 seq를 저장할 리스트
    List<String> spinnerList = new ArrayList<>();
    List<Integer> spinnerSeqList = new ArrayList<>();

    TextView spinnerValueTv;
    ImageView imageView;
    RelativeLayout relativeLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_page);

        spinnerValueTv = (TextView) findViewById(R.id.spinnerValue);
        spinner = (Spinner) findViewById(R.id.spinner);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imageView = (ImageView) findViewById(R.id.loadingImage);
        relativeLayout = (RelativeLayout) findViewById(R.id.loadingPage);

        // Gson 및 Retrofit 이용해 service(API) 만들기
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(home_coffee_service.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        coffeeAPI = retrofit.create(home_coffee_service.class);

        // 자료 가져옴
        coffeeAPI.getAllDTO().enqueue(new Callback<List<DTO_home_coffee>>() {
            @Override
            public void onResponse(Call<List<DTO_home_coffee>> call, Response<List<DTO_home_coffee>> response) {
                if(response.isSuccessful()) {
                    List<DTO_home_coffee> listDto = response.body();
                    // 스피너 속 값들 설정
                    for (DTO_home_coffee dto : listDto) {
                        spinnerList.add(dto.getNm());
                        spinnerSeqList.add(dto.getSeq());
                    }

                    // 스피너 셋팅
                    adapterSpinner = new adapter_spinner(getApplicationContext(), spinnerList);
                    spinner.setAdapter(adapterSpinner);

                    // 스피너 값 선택 이벤트
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // 어댑터에서 정의한 메서드를 통해 스피너에서 선택한 아이템의 이름을 받아온다.
                            selectedItem = (String) adapterSpinner.getItem(position);
                            selectedSeq = spinnerSeqList.get(position);
                            spinnerValueTv.setText(selectedItem + " & seq 값은 : " + selectedSeq);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<DTO_home_coffee>> call, Throwable t) {

            }
        });




        // 앨범으로 이동 > 커스텀 갤러리 만들기는 나중에
        Button choiceBtn = (Button) findViewById(R.id.choiceBtn);
        choiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 이미지를 가져올 수 있도록 셋팅
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, 200);
        });

        // 사진 타이틀 폴더로 올리기
        Button titleUploadBtn = (Button) findViewById(R.id.title_uploadBtn);
        titleUploadBtn.setOnClickListener(v -> {
            uploadImage("0");
        });

        // 선택한 사진 서버로 올리기
        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(v -> {
            uploadImage("1");
        });
    }

    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) {
            // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null) {
                // 이미지를 하나만 선택한 경우
                Log.e("single choice : ", String.valueOf(data.getData()));
                // data에서 절대경로로 이미지를 가져옴
                Uri imageUri = data.getData();

                uriList.add(imageUri);

                adapter = new adapter_multi_image(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            } else {
                // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));
                if(clipData.getItemCount() > 10) {
                    // 사진을 11장 이상 선택한 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 선택한 사진이 10장 이하인 경우
                    Log.e("TAG", "multiple choice");
                    for(int i=0; i<clipData.getItemCount(); i++) {
                        // 선택한 이미지들의 uri를 가져온다
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        try {
                            // uri를 list에 담는다.
                            uriList.add(imageUri);
                        } catch (Exception e) {
                            Log.e("TAG", "File select error", e);
                        }
                    }

                    adapter = new adapter_multi_image(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                }
            }
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void uploadImage(String condition) {
        imageView.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);

        // 같이 보낼 데이터 준비
        String uploader = "admin"; // 저장하는 사람
        String seq = String.valueOf(selectedSeq); // 선택한 번호 (이미지와 카페 연결)

        for(Uri uri : uriList) {
            // 날짜 데이터 생성
            Date nowDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            //원하는 데이터 포맷 지정
            String strNowDate = simpleDateFormat.format(nowDate);

            // 사진의 루트 가져와서 File 객체 생성
            String imagePath = getRealPathFromUri(uri);
            File file = new File(imagePath);

            // MultipartBody 형식으로 만들어 ArrayList 모으기
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files",  uploader + "_" + strNowDate + "_" + file.getName(), requestBody);
            bodyList.add(uploadFile);
        }

        try {
            // 같이 보낼 데이터 hasMap 형식으로 제작
            Map<String, String> map = new HashMap<>();

            map.put("seq", seq);
            map.put("uploader", uploader);
            map.put("condition", condition);

            // 동기 실행
            coffeeAPI.uploadMultipleFiles(bodyList, map).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    uploadResponse(0);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    uploadResponse(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadResponse(int num) {
        imageView.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        uriList.clear();
        bodyList.clear();
        adapter.notifyDataSetChanged();

        switch (num) {
            case 0 :
                Toast.makeText(Activity_UploadPage.this, "이미지 업로드 완료", Toast.LENGTH_SHORT).show();

            case 1 :
                Toast.makeText(Activity_UploadPage.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
        }
    }
}