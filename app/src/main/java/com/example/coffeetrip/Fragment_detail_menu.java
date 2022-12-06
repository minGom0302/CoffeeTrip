package com.example.coffeetrip;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coffeetrip.Adapter.adapter_detail_menu;
import com.example.coffeetrip.Adapter.adapter_home_coffee;
import com.example.coffeetrip.DTO.DTO_detail_menu;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_detail_menu extends Fragment {
    RecyclerView menuRecyclerView;
    adapter_detail_menu menuAdapter;
    home_coffee_service menuAPI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_menu, container, false);

        // 엔텐트 값 받아오기
        Intent intent = getActivity().getIntent();
        int seq = intent.getIntExtra("seq", 0);

        menuRecyclerView = (RecyclerView) view.findViewById(R.id.detail_menu_recyclerView);
        menuAPI = useItem.getRetrofit().create(home_coffee_service.class);

        getImage(seq);
        return view;
    }

    private void getImage(int seq) {

        menuAPI.getDetailMenu(seq).enqueue(new Callback<List<DTO_detail_menu>>() {
            @Override
            public void onResponse(Call<List<DTO_detail_menu>> call, Response<List<DTO_detail_menu>> response) {
                if(response.isSuccessful()) {
                    List<DTO_detail_menu> menuList = response.body();
                    menuAdapter = new adapter_detail_menu(menuList);
                    menuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    menuRecyclerView.setAdapter(menuAdapter);
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<DTO_detail_menu>> call, Throwable t) {

            }
        });
    }
}