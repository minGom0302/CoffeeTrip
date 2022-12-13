package com.example.coffeetrip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.Adapter.adapter_detail_review;
import com.example.coffeetrip.DTO.DTO_detail_review;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;

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

public class Fragment_detail_review extends Fragment {
    Button onOffBtn, imageChoiceBtn, submitBtn;
    TextView nickNameTv, ratingTv, imageYN;
    EditText reviewEt;
    FrameLayout frameLayout;
    LinearLayout reviewLayout;
    RecyclerView reviewRecyclerView;
    RatingBar ratingBar;
    ImageView thumbnail;

    home_coffee_service reviewAPI;
    adapter_detail_review reviewAdapter;
    SharedPreferences sp;
    Uri imageUri;
    ProgressDialog loadingDialog;

    String nickName, userId;
    int seq;

    @SuppressLint({"MissingInflatedId", "IntentReset"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_review, container, false);

        Intent intent = getActivity().getIntent();
        seq = intent.getIntExtra("seq", 0);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        onOffBtn = (Button) view.findViewById(R.id.detail_review_reviewOnOffBtn);
        imageChoiceBtn = (Button) view.findViewById(R.id.detail_review_imageChoiceBtn);
        submitBtn = (Button) view.findViewById(R.id.detail_review_submitBtn);
        nickNameTv = (TextView) view.findViewById(R.id.detail_review_nickNameTv);
        ratingTv = (TextView) view.findViewById(R.id.detail_review_ratingTv);
        imageYN = (TextView) view.findViewById(R.id.detail_review_imageSelectedYN);
        reviewEt = (EditText) view.findViewById(R.id.detail_review_reviewEt);
        ratingBar = (RatingBar) view.findViewById(R.id.detail_review_ratingbar);
        thumbnail = (ImageView) view.findViewById(R.id.detail_review_thumbnail);
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_detail_review_layout);
        reviewLayout = (LinearLayout) view.findViewById(R.id.detail_review_reviewLayout);
        reviewRecyclerView = (RecyclerView) view.findViewById(R.id.detail_review_recyclerView);

        reviewAPI = useItem.getRetrofit().create(home_coffee_service.class);

        userId = sp.getString("id", null);
        nickName = sp.getString("nickname", null);
        nickNameTv.setText(nickName);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                useItem.editTextHide(getActivity());
                // 1로 나눴을 때 나머지를 구해서 정수면 int 타입으로 바꿔서 .0 없애고 표기 실수면 소수점 그대로 표기
                if(rating % 1 == 0) {
                    ratingTv.setText(String.valueOf((int) rating));
                } else {
                    ratingTv.setText(String.valueOf(rating));
                }
            }
        });

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                useItem.editTextHide(getActivity());
                return false;
            }
        });

        onOffBtn.setOnClickListener(v -> {
            if(reviewLayout.getVisibility() == View.VISIBLE) {
                reviewLayout.setVisibility(View.GONE);
                onOffBtn.setText("리뷰 남기기");
            } else if(reviewLayout.getVisibility() == View.GONE) {
                reviewLayout.setVisibility(View.VISIBLE);
                onOffBtn.setText("리뷰 취소");
            }
        });

        imageChoiceBtn.setOnClickListener(v -> {
            imageUri = null;
            Intent imageIntent = new Intent(Intent.ACTION_PICK);
            imageIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageIntent.setType("image/*");
            startActivityForResult(imageIntent, 200);
        });

        submitBtn.setOnClickListener(v -> {
            loadingDialog = ProgressDialog.show(getContext(), "리뷰 등록중 ...", "Please Wait...", true, false);
            uploadReview();
        });

        setRecyclerView(seq);
        return view;
    }

    private void setRecyclerView(int seq) {
        reviewAPI.getDetailReview(seq).enqueue(new Callback<List<DTO_detail_review>>() {
            @Override
            public void onResponse(Call<List<DTO_detail_review>> call, Response<List<DTO_detail_review>> response) {
                if(response.isSuccessful()) {
                    List<DTO_detail_review> reviewList = response.body();
                    reviewAdapter = new adapter_detail_review(reviewList);
                    reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    reviewRecyclerView.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<DTO_detail_review>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) {
            // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "이미지를 선택했습니다.", Toast.LENGTH_SHORT).show();
            imageYN.setVisibility(View.VISIBLE);
            thumbnail.setVisibility(View.VISIBLE);
            imageUri = data.getData();
            Glide.with(getContext()).load(imageUri).into(thumbnail);
        }
    }

    public void uploadReview() {
        // 등록 날짜 생성
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String strNowDate = simpleDateFormat.format(nowDate);

        // 같이 보낼 데이터 준비
        String review = String.valueOf(reviewEt.getText());
        String rating = String.valueOf(ratingTv.getText());
        String shopSeq = String.valueOf(seq);
        Map<String, String> reviewMap = new HashMap<>();
        reviewMap.put("userId", userId);
        reviewMap.put("nickName", nickName);
        reviewMap.put("review", review);
        reviewMap.put("rating", rating);
        reviewMap.put("shopSeq", shopSeq);
        reviewMap.put("date", strNowDate);

        ArrayList<MultipartBody.Part> bodyList = new ArrayList<>();
        // 포맷 지정
        if(imageUri != null) {
            // 사진의 루트 가져와서 File 객체 생성, 사진 이름만 얻어오기
            String imageName = getRealPathFromUri(imageUri);
            File file = new File(imageName);
            // MultipartBody 형식으로 만들어 ArrayList 모으기
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files", userId+ "_" + strNowDate + "_" + file.getName(), requestBody);
            bodyList.add(uploadFile);
        }

        home_coffee_service reviewAPI = useItem.getRetrofit().create(home_coffee_service.class);
        reviewAPI.uploadReviewFiles(bodyList, reviewMap).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    Log.i("reviewAPI", "response successful");
                } else {
                    Log.i("reviewAPI", "response fail");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 왜 응답이 Failure로 오는지 모르겠다. 저쪽 return 값을 String으로 줘서 그런가 생각중
                Log.i("reviewAPI", "failure");

                setRecyclerView(seq);
                ratingBar.setRating(0);
                ratingTv.setText("0");
                reviewEt.setText("");
                imageYN.setVisibility(View.INVISIBLE);
                thumbnail.setVisibility(View.GONE);
                reviewLayout.setVisibility(View.GONE);
                onOffBtn.setText("리뷰 남기기");
                imageUri = null;
                loadingDialog.dismiss();
            }
        });
    }

    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}