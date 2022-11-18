package com.example.coffeetrip;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class Fragment_main_home extends Fragment {
    private final String TAG = "frag_main_home.java : ";
    RecyclerView coffeeRecyclerView;
    List<DTO_home_coffee> listDTO;
    adapter_home_coffee coffeeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        // 리사이클러뷰 찾아오기
        coffeeRecyclerView = (RecyclerView)view.findViewById(R.id.main_home_coffee_recyclerView);

        // 통신 시 JSON 사용과 파싱을 위한 생성
        Gson gson = new GsonBuilder().setLenient().create();

        //Retrofit 구현
        Retrofit retrofit = new Retrofit.Builder()
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
                    coffeeAdapter = new adapter_home_coffee(listDTO);
                    coffeeRecyclerView.setAdapter(coffeeAdapter);
                    // 주석 처리한 건 세로 안된 건 가로로 리스트가 생김
                    // coffeeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    coffeeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

                } else {
                    Log.i(TAG, "응답 실패");
                }
            }

            @Override
            public void onFailure(Call<List<DTO_home_coffee>> call, Throwable t) {
                Log.i(TAG, "응답 실패 : " + t.getMessage());
            }
        });

        return view;
    }
}