package com.example.coffeetrip;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.Adapter.adapter_home_coffee;
import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_main_home extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "frag_main_home.java : ";
    Gson gson;
    Retrofit retrofit;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView coffeeRecyclerView;
    adapter_home_coffee coffeeAdapter;

    List<DTO_home_coffee> listDTO;

    ImageView imageView;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_home_swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        imageView = view.findViewById(R.id.listview_main_home_coffee_backImage);

        // 리사이클러뷰 찾아오기
        coffeeRecyclerView = (RecyclerView)view.findViewById(R.id.main_home_coffee_recyclerView);

        // 통신 시 JSON 사용과 파싱을 위한 생성
        gson = new GsonBuilder().setLenient().create();

        // 데이터 가져와서 화면에 표현 (첫 줄 내 주변 카페)
        getDataCoffee();

        return view;
    }

    @Override
    public void onRefresh() {
        // 데이터 다시 가져옴
        getDataCoffee();
        // 새로고침 완료 처리
        swipeRefreshLayout.setRefreshing(false);
    }

    // 첫 줄 내 주변 카페 데이터를 가져오는 메소드
    public void getDataCoffee() {
        //Retrofit 구현
        retrofit = new Retrofit.Builder()
                .baseUrl(home_coffee_service.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        home_coffee_service coffeeAPI = retrofit.create(home_coffee_service.class);
        // 비동기 통신 실행
        Log.i(TAG, "비동기 실행");
        coffeeAPI.getAllDTO().enqueue(new Callback<List<DTO_home_coffee>>() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onResponse(Call<List<DTO_home_coffee>> call, Response<List<DTO_home_coffee>> response) {
                Log.i(TAG, "응답중");
                if(response.isSuccessful()) {
                    Log.i(TAG, "응답 성공");

                    // 자료를 가져옴
                    listDTO = response.body();
                    Log.i(TAG, String.valueOf(listDTO));

                    // adpater에 listDTO 넣어서 설정
                    coffeeAdapter = new adapter_home_coffee(listDTO);;
                    // 주석 처리한 건 세로 안된 건 가로로 리스트가 생김
                    // coffeeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    coffeeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

                    // 클릭이벤트 가져와서 재설정
                    coffeeAdapter.setOnItemClickListener(new adapter_home_coffee.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, DTO_home_coffee dto) {
                            Toast.makeText(getContext(), "커피집 이름 : " + dto.nm, Toast.LENGTH_SHORT).show();
                        }
                    });

                    coffeeRecyclerView.setAdapter(coffeeAdapter);

                } else {
                    Log.i(TAG, "응답 실패");
                }
            }

            @Override
            public void onFailure(Call<List<DTO_home_coffee>> call, Throwable t) {
                Log.i(TAG, "응답 실패 : " + t.getMessage());
            }
        });
    }
}