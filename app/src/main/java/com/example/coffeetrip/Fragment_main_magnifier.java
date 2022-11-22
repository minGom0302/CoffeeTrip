package com.example.coffeetrip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffeetrip.DTO.DTO_magnifier;
import com.example.coffeetrip.Interface.magnifier_service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_main_magnifier extends Fragment implements OnMapReadyCallback {
    private String TAG = "Fragment_main_magnifier : ";
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    FragmentManager fm;
    MapFragment mfm;
    List<DTO_magnifier> listDTO;
    magnifier_service API;
    Retrofit retrofit;
    Gson gson;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_magnifier, container, false);
        Log.i(TAG, "onCreateView");

        if(fm != null || mfm != null) {
            fm = null;
            mfm = null;
        }
        // 지도 객체 생성
        fm = getActivity().getSupportFragmentManager();
        mfm = (MapFragment) fm.findFragmentById(R.id.magnifier_map_fragment);
        if(mfm == null) {
            mfm = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.magnifier_map_fragment, mfm).commit();
        }

        // 위치 반환하는 구현체 : FusedLocationSource 생성
        locationSource = new FusedLocationSource(getActivity(), LOCATION_PERMISSION_REQUEST_CODE);


        // 비동기로 onMapReady 콜백 배서드 호출 (naverMap 객체를 받음)
        mfm.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.i(TAG, "onMapReady");

        this.naverMap = naverMap;
        // 네이버 셋팅
        naverMap.setMapType(NaverMap.MapType.Navi);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false);

        // 데이터 가져와서 화면에 마커찍기
        setMarkers();
        // 현재 위치 설정
        naverMap.setLocationSource(locationSource);
        // 권한 확인
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
        // 현재 위치 표시할 때 마커로 표시
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        // 네이버지도 UI 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setZoomControlEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);
    }

    private void setMarkers() {
        Log.i(TAG, "setMarkers");
        // 초기화
        if(listDTO != null) {
            listDTO.clear();
        }
        if(retrofit != null) {
            retrofit = null;
        }
        if(gson != null) {
            gson = null;
        }
        // DB에서 자료 가져오기
        // 통신 시 JSON 사용과 파싱을 위한 생성
        gson = new GsonBuilder().setLenient().create();

        //Retrofit 구현
        retrofit = new Retrofit.Builder()
                .baseUrl(magnifier_service.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        API = retrofit.create(magnifier_service.class);

        // 비동기 통신 실행
        API.getAddressDTO().enqueue(new Callback<List<DTO_magnifier>>() {
            @Override
            public void onResponse(Call<List<DTO_magnifier>> call, Response<List<DTO_magnifier>> response) {
                if(response.isSuccessful()) {
                    // 자료를 가져옴
                    listDTO = response.body();

                    for(int i=0; i<listDTO.size(); i++) {
                        Marker marker = new Marker();
                        DTO_magnifier dto = listDTO.get(i);
                        // 마커 위치
                        marker.setPosition(new LatLng(dto.lat, dto.lng));
                        // 마커 크기
                        marker.setWidth(marker.SIZE_AUTO);
                        marker.setHeight(marker.SIZE_AUTO);
                        // 마커 아이콘
                        marker.setIcon(OverlayImage.fromResource(R.drawable.location_red));
                        // 마커 텍스트
                        marker.setCaptionText(dto.nm);
                        // 마커 텍스트 가로 사이즈
                        marker.setCaptionRequestedWidth(200);
                        // 마커 표시
                        marker.setMap(naverMap);
                    }

                } else {
                    Log.i(TAG, "응답 실패");
                }
            }

            @Override
            public void onFailure(Call<List<DTO_magnifier>> call, Throwable t) {
                Log.i(TAG, "응답 실패 : " + t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if(!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            } else {

            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}