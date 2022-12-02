package com.example.coffeetrip;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.Adapter.adapter_home_coffee;
import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_main_home extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnTouchListener {
    private final String TAG = "frag_main_home.java : ";
    Gson gson;
    Retrofit retrofit;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView coffeeRecyclerView, recentlyRecyclerView;
    adapter_home_coffee coffeeAdapter, recentlyAdapter;

    List<DTO_home_coffee> top_listDTO, recently_listDTO;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        ScrollView scrollView = view.findViewById(R.id.main_home_scrollView);
        // 리사이클러뷰 찾아오기
        coffeeRecyclerView = (RecyclerView) view.findViewById(R.id.main_home_coffee_recyclerView);
        recentlyRecyclerView = (RecyclerView) view.findViewById(R.id.main_home_coffee_recyclerView_recently);
        // swiper 찾아와서 셋팅
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_home_swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setDistanceToTriggerSync(200); // 새로고침 민감도

        coffeeRecyclerView.setOnTouchListener(this);

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
        home_coffee_service coffeeAPI = useItem.getRetrofit().create(home_coffee_service.class);
        // 비동기 통신 실행
        Log.i(TAG, "비동기 실행");
        coffeeAPI.getTopTenDTO().enqueue(new Callback<List<DTO_home_coffee>>() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onResponse(Call<List<DTO_home_coffee>> call, Response<List<DTO_home_coffee>> response) {
                if(response.isSuccessful()) {
                    // 자료를 가져옴
                    top_listDTO = response.body();

                    // adpater에 listDTO 넣어서 설정
                    coffeeAdapter = new adapter_home_coffee(top_listDTO);;
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

        coffeeAPI.getRecentlyCafeDTO().enqueue(new Callback<List<DTO_home_coffee>>() {
            @Override
            public void onResponse(Call<List<DTO_home_coffee>> call, Response<List<DTO_home_coffee>> response) {
                if(response.isSuccessful()) {
                    // 자료를 가져옴
                    recently_listDTO = response.body();

                    // adpater에 listDTO 넣어서 설정
                    recentlyAdapter = new adapter_home_coffee(recently_listDTO);;
                    // 주석 처리한 건 세로 안된 건 가로로 리스트가 생김
                    // coffeeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recentlyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

                    // 클릭이벤트 가져와서 재설정
                    recentlyAdapter.setOnItemClickListener(new adapter_home_coffee.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position, DTO_home_coffee dto) {
                            Toast.makeText(getContext(), "커피집 이름 : " + dto.nm, Toast.LENGTH_SHORT).show();
                        }
                    });

                    recentlyRecyclerView.setAdapter(recentlyAdapter);
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

    // 터치 이벤트
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_SCROLL :
            case MotionEvent.ACTION_MOVE :
                swipeRefreshLayout.setEnabled(false);
                break;
            case MotionEvent.ACTION_UP:
                swipeRefreshLayout.setEnabled(true);
                break;
        }
        return false;
    }
}