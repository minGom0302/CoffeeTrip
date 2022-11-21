package com.example.coffeetrip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;


public class Fragment_main_magnifier extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    public double lat, lng;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_magnifier, container, false);

        // 맵뷰를 찾아서 맵 생성
        mapView = view.findViewById(R.id.magnifier_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        locationSource = new FusedLocationSource(getActivity(), LOCATION_PERMISSION_REQUEST_CODE);

        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if(!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        // 현재 위치 설정
        naverMap.setLocationSource(locationSource);
        // 현재 위치 표시할 때 마커로 표시
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
        // 권한 확인
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);

        // 네이버지도 UI 설정
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setZoomControlEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);

        // 내위치 찾기
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission")
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = location.getLatitude();
        lng = location.getLongitude();

        // 위에서 찾은 위치로 화면(카메라) 이동
        CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lng), 14);
        naverMap.setCameraPosition(cameraPosition);

        /* 화면 이동될 때 발생하는 메서드
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        });
        */
    }
}