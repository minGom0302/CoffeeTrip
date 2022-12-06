package com.example.coffeetrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_DetailPage extends AppCompatActivity implements OnMapReadyCallback {
    private static String TAG = "detailActivity : ";
    MapView mapView;
    TextView titleTv, likeCountTv, addressTv, menuBtn, imageBtn, reviewBtn, rating;
    ImageButton callBtn, likeBtn, shareBtn;
    ImageView menuLine, imageLine, reviewLine;
    RatingBar ratingBar;
    AppBarLayout appBarLayout;

    GoogleMap mMap;
    double nowLat, nowLng;
    boolean likeYN;
    DTO_home_coffee data;
    home_coffee_service coffeeAPI;
    String myId;

    SharedPreferences sp;
    Fragment_detail_menu f01 = null;
    Fragment_detail_image f02 = null;
    Fragment_detail_review f03 = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        // 핸드폰 내부에 저장된 것 꺼내오기
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        myId = sp.getString("id", "");

        mapView = (MapView) findViewById(R.id.detail_mapView);
        rating = (TextView) findViewById(R.id.detail_rating);
        ratingBar = (RatingBar) findViewById(R.id.detail_ratingBar);
        titleTv = (TextView) findViewById(R.id.detail_title);
        likeCountTv = (TextView) findViewById(R.id.detail_likeCount);
        addressTv = (TextView) findViewById(R.id.detail_address);
        callBtn = (ImageButton) findViewById(R.id.detail_call);
        likeBtn = (ImageButton) findViewById(R.id.detail_like);
        shareBtn = (ImageButton) findViewById(R.id.detail_share);
        menuBtn = (TextView) findViewById(R.id.detail_menu);
        imageBtn = (TextView) findViewById(R.id.detail_image);
        reviewBtn = (TextView) findViewById(R.id.detail_review);
        menuLine = (ImageView) findViewById(R.id.detail_menu_line);
        imageLine = (ImageView) findViewById(R.id.detail_image_line);
        reviewLine = (ImageView) findViewById(R.id.detail_review_line);
        appBarLayout = (AppBarLayout) findViewById(R.id.detail_appBarLayout);

        callBtn.setOnClickListener(v -> { buttonClick(0);} );
        likeBtn.setOnClickListener(v -> { buttonClick(1);} );
        shareBtn.setOnClickListener(v -> { buttonClick(2);} );
        menuBtn.setOnClickListener(v -> { buttonClick(3);} );
        imageBtn.setOnClickListener(v -> { buttonClick(4);} );
        reviewBtn.setOnClickListener(v -> { buttonClick(5);} );

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); // onMapReady() 호출


        Intent intent = getIntent();
        int seq = intent.getIntExtra("seq", 0);

        // 첫 화면으로 fragment_detail_menu.xml 띄우면서 텍스트에 밑줄 추가
        f01 = new Fragment_detail_menu();
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_frameLayout, f01).commit();
        imageLine.setVisibility(View.INVISIBLE);
        reviewLine.setVisibility(View.INVISIBLE);

        // 정보 가져오기
        coffeeAPI = useItem.getRetrofit().create(home_coffee_service.class);
        Log.i(TAG, String.valueOf(seq));
        coffeeAPI.getDetailInfo(seq, myId).enqueue(new Callback<DTO_home_coffee>() {
            @Override
            public void onResponse(Call<DTO_home_coffee> call, Response<DTO_home_coffee> response) {
                if(response.isSuccessful()) {
                    data = response.body();
                    titleTv.setText(data.nm);
                    addressTv.setText(data.address);
                    likeCountTv.setText("좋아요 : " + data.favorite);
                    likeYN = data.favoriteYN;
                    Log.i(TAG, String.valueOf(likeYN));
                    if(likeYN) {
                        likeBtn.setImageResource(R.drawable.icon_favorite);
                    } else {
                        likeBtn.setImageResource(R.drawable.icon_unfavorite);
                    }
                    nowLat = data.lat;
                    nowLng = data.lng;
                    setMap();
                    rating.setText(String.valueOf(data.rating));
                    ratingBar.setRating((float) data.rating);
                    getSupportActionBar().setTitle(data.nm);
                }

            }

            @Override
            public void onFailure(Call<DTO_home_coffee> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // 구글맵 드레그 할 경우 움직이기 위해 AppBarLayout 드레그할 경우 아무것도 안하도록 설정
        if (appBarLayout.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }
        appBarLayout.setExpanded(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    public void setMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(nowLat, nowLng), 16));
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(nowLat, nowLng));
        mMap.addMarker(marker);
    }

    // 버튼 클릭 이벤트
    public void buttonClick(int num) {
        switch (num) {
            case 0 :
                Intent intentCall = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + data.tel));
                startActivity(intentCall);
                break;
            case 1 :
                if(likeYN) {
                    likeYN = false;
                    likeBtn.setImageResource(R.drawable.icon_unfavorite);
                    coffeeAPI.minusFavorite(data.seq, myId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) {
                                Toast.makeText(Activity_DetailPage.this, "좋아요를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                    int like = data.favorite - 1;
                    likeCountTv.setText("좋아요 : " + like);
                } else {
                    likeYN = true;
                    likeBtn.setImageResource(R.drawable.icon_favorite);
                    coffeeAPI.plusFavorite(data.seq, myId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) {
                                Toast.makeText(Activity_DetailPage.this, "좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                    int like = data.favorite + 1;
                    likeCountTv.setText("좋아요 : " + like);
                }
                break;
            case 2 :
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                String msg = "공유 링크 준비중입니다.";
                intentShare.addCategory(Intent.CATEGORY_DEFAULT);
                intentShare.putExtra(Intent.EXTRA_TEXT, msg);
                intentShare.setType("text/plain");
                startActivity(Intent.createChooser(intentShare, "앱을 선택해주세요."));
                break;
            case 3 :
                if (f01 == null) {
                    f01 = new Fragment_detail_menu();
                    getSupportFragmentManager().beginTransaction().add(R.id.detail_frameLayout, f01).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(f01).commit();
                }
                if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                // 버튼 텍스트 두껍게하고 밑줄 긋기
                menuBtn.setTypeface(null, Typeface.BOLD);
                imageBtn.setTypeface(null, Typeface.NORMAL);
                reviewBtn.setTypeface(null, Typeface.NORMAL);
                menuBtn.setTextColor(Color.parseColor("#000000"));
                imageBtn.setTextColor(Color.parseColor("#737373"));
                reviewBtn.setTextColor(Color.parseColor("#737373"));
                menuLine.setVisibility(View.VISIBLE);
                imageLine.setVisibility(View.INVISIBLE);
                reviewLine.setVisibility(View.INVISIBLE);
                break;
            case 4 :
                if(f02 == null) {
                    Log.i(TAG, "f02 null");
                    f02 = new Fragment_detail_image();
                    getSupportFragmentManager().beginTransaction().add(R.id.detail_frameLayout, f02).commit();
                } else {
                    Log.i(TAG, "f02 not null");
                    getSupportFragmentManager().beginTransaction().show(f02).commit();
                }
                if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                // 버튼 텍스트 두껍게하고 밑줄 긋기
                menuBtn.setTypeface(null, Typeface.NORMAL);
                imageBtn.setTypeface(null, Typeface.BOLD);
                reviewBtn.setTypeface(null, Typeface.NORMAL);
                menuBtn.setTextColor(Color.parseColor("#737373"));
                imageBtn.setTextColor(Color.parseColor("#000000"));
                reviewBtn.setTextColor(Color.parseColor("#737373"));
                menuLine.setVisibility(View.INVISIBLE);
                imageLine.setVisibility(View.VISIBLE);
                reviewLine.setVisibility(View.INVISIBLE);
                break;
            case 5 :
                if(f03 == null) {
                    Log.i(TAG, "f03 null");
                    f03 = new Fragment_detail_review();
                    getSupportFragmentManager().beginTransaction().add(R.id.detail_frameLayout, f03).commit();
                } else {
                    Log.i(TAG, "f03 not null");
                    getSupportFragmentManager().beginTransaction().show(f03).commit();
                }
                if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                // 버튼 텍스트 두껍게하고 밑줄 긋기
                menuBtn.setTypeface(null, Typeface.NORMAL);
                imageBtn.setTypeface(null, Typeface.NORMAL);
                reviewBtn.setTypeface(null, Typeface.BOLD);
                menuBtn.setTextColor(Color.parseColor("#737373"));
                imageBtn.setTextColor(Color.parseColor("#737373"));
                reviewBtn.setTextColor(Color.parseColor("#000000"));
                menuLine.setVisibility(View.INVISIBLE);
                imageLine.setVisibility(View.INVISIBLE);
                reviewLine.setVisibility(View.VISIBLE);
                break;
        }
    }
}