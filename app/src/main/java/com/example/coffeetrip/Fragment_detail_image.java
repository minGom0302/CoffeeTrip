package com.example.coffeetrip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.example.coffeetrip.Adapter.adapter_detail_image;
import com.example.coffeetrip.DTO.DTO_detail_image;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_detail_image extends Fragment {
    String TAG = "fragment_detail_image";
    RecyclerView recyclerView;
    adapter_detail_image adapterDetailImage;
    home_coffee_service coffeeAPI;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_image, container, false);

        Intent intent = getActivity().getIntent();
        int seq = intent.getIntExtra("seq", 0);
        Log.i(TAG, String.valueOf(seq));

        recyclerView = (RecyclerView) view.findViewById(R.id.detail_image_recyclerView);
        coffeeAPI = useItem.getRetrofit().create(home_coffee_service.class);

        getImage(seq);
        return view;
    }

    private void getImage(int seq) {
        coffeeAPI.getDetailImage(seq, 1).enqueue(new Callback<List<DTO_detail_image>>() {
            @Override
            public void onResponse(Call<List<DTO_detail_image>> call, Response<List<DTO_detail_image>> response) {
                if(response.isSuccessful()) {
                    List<DTO_detail_image> imageList = response.body();
                    for(DTO_detail_image i : imageList) {
                        Log.i(TAG, "여기부터 / "+i.getFileName());
                    }
                    //recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    adapterDetailImage = new adapter_detail_image(imageList);
                    recyclerView.setAdapter(adapterDetailImage);

                }
            }

            @Override
            public void onFailure(Call<List<DTO_detail_image>> call, Throwable t) {

            }
        });
    }
}