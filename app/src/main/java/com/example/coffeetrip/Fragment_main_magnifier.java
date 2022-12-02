package com.example.coffeetrip;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coffeetrip.DTO.DTO_magnifier;
import com.example.coffeetrip.use.useItem;
import com.example.coffeetrip.use.zoomLevel;
import com.example.coffeetrip.Interface.magnifier_service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_main_magnifier extends Fragment implements OnMapReadyCallback {
    private static final int DEFAULT_ZOOM = 15;
    private String TAG = "Fragment_main_magnifier : ";
    private GoogleMap googleMap;
    private MapView mapView;

    Button btn;

    List<DTO_magnifier> listDTO;
    magnifier_service API;

    //double lat, lng;
    double nowLat, nowLng;
    float zoomLevel;
    boolean check = true;

    @SuppressLint({"MissingInflatedId", "MissingPermission"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_magnifier, container, false);
        Log.i(TAG, "onCreateView");

        // 지도 객체 설정
        mapView = new MapView(this.getActivity());
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        mapView.onCreate(savedInstanceState);

        // onMapReady() 호출
        mapView.getMapAsync(this);

        // refresh 버튼 설정
        btn = (Button) view.findViewById(R.id.main_magnifier_refreshBtn);
        btn.setOnClickListener(v -> {
            setMarkers(); // 마커표시 다시하기
            btn.setVisibility(btn.INVISIBLE); // 버튼 숨김
        });

        return view;
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
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // 카메라(화면)의 움직임이 발생했을 때
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(@NonNull CameraPosition cameraPosition) {
                zoomLevel = googleMap.getCameraPosition().zoom; // 줌 레벨 가져옴
                btn.setVisibility(btn.VISIBLE); // 버튼 보임
                LatLng nowCenter = cameraPosition.target; // 현재 화면 중앙의 위치(좌표)를 가져옴
                nowLat = nowCenter.latitude; // 현재 화면 중심 좌표의 위도
                nowLng = nowCenter.longitude; // 현재 화면 중심 좌표의 경도
            }
        });

        // 구글 맵 셋팅
        setGoogleMap();

        // DB에서 데이터 가져와서 화면에 마커찍기
        setMarkers();
    }


    @SuppressLint("MissingPermission")
    // 구글 맵 관련 모든 설정 메소드
    private void setGoogleMap() {
        // 첫 화면을 서울 시청으로 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.5665734, 126.978179), DEFAULT_ZOOM));
        // 객체 생성
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기 > 지도 화면으로 이동
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                gpsLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                gpsLocationListener);

        // 구글맵 형태 (위성, 산악, 기본) 선택
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Ui setting : 내위치 버튼, 줌 컨트롤 버튼 등 설정
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        // MyLocation 버튼 생성
        googleMap.setMyLocationEnabled(true);

        // try 문 : 구글맵 스타일과 표시 지정 -> res/raw/style_json 파일에 작성하여 지도에 road, landmark, label, theme 설정
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }
    }
    // GPS 움직임이 감지될 때
    public final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();

            // 화면 카메라 이동 처음만 이동
            if(check) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
                nowLat = location.getLatitude();
                nowLng = location.getLongitude();
                setMarkers();

                check = false;
            }
        }
    };

    // DB에서 데이터 가져와서 화면에 마커찍기
    private void setMarkers() {
        Log.i(TAG, "setMarkers");
        // 초기화
        if(listDTO != null) {
            listDTO.clear();
        }
        googleMap.clear();

        API = useItem.getRetrofit().create(magnifier_service.class);

        // 현재 줌 레벨을 가지고 zoomLevel 파일에서 반경(kilometer)를 가져옴
        zoomLevel zoom = new zoomLevel();
        double distance = zoom.kilometer(zoomLevel);

        // 비동기 통신 실행
        API.getDataDistance(nowLat, nowLng, distance).enqueue(new Callback<List<DTO_magnifier>>() {
            @Override
            public void onResponse(Call<List<DTO_magnifier>> call, Response<List<DTO_magnifier>> response) {
                if(response.isSuccessful()) {
                    // 자료를 가져옴옴
                   listDTO = response.body();

                    for(int i=0; i<listDTO.size(); i++) {
                        DTO_magnifier dto = listDTO.get(i);
                        MarkerOptions marker = new MarkerOptions();
                        marker.position(new LatLng(dto.getLat(), dto.getLng()));
                        marker.title(dto.getNm());
                        googleMap.addMarker(marker);
                    }

                } else {
                    Log.i(TAG, "응답 실패");
                }
            }

            @Override
            public void onFailure(Call<List<DTO_magnifier>> call, Throwable t) {

            }
        });
    }
}