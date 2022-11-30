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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.coffeetrip.Adapter.adapter_multi_image;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    ImageView imageView;
    RelativeLayout relativeLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_page);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imageView = (ImageView) findViewById(R.id.loadingImage);
        relativeLayout = (RelativeLayout) findViewById(R.id.loadingPage);

        // Gson 및 Retrofit 이용해 service(API) 만들기
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(home_coffee_service.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        home_coffee_service coffeeAPI = retrofit.create(home_coffee_service.class);

        // 앨범으로 이동 > 커스텀 갤러리 만들기는 나중에
        Button choiceBtn = (Button) findViewById(R.id.choiceBtn);
        choiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 이미지를 가져올 수 있도록 셋팅
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, 200);
        });

        // 선택한 사진 서버로 올리기
        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(v -> {
            imageView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.INVISIBLE);
            String id = "admin_";
            for(Uri uri : uriList) {
                // 사진의 루트 가져와서 File 객체 생성
                String imagePath = getRealPathFromUri(uri);
                File file = new File(imagePath);

                // MultipartBody 형식으로 만들어 ArrayList 모으기
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files",  id + file.getName(), requestBody);
                bodyList.add(uploadFile);
            }

            try {
                // 동기 실행
                coffeeAPI.uploadMultipleFiles(bodyList).enqueue(new Callback<String>() {
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

    private void uploadResponse(int num) {
        imageView.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        adapter.clearItems();
        adapter.notifyDataSetChanged();

        switch (num) {
            case 0 :
                Toast.makeText(Activity_UploadPage.this, "이미지 업로드 완료", Toast.LENGTH_SHORT).show();

            case 1 :
                Toast.makeText(Activity_UploadPage.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
        }
    }
}