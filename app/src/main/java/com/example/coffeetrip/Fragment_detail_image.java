package com.example.coffeetrip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffeetrip.Adapter.adapter_detail_image;
import com.example.coffeetrip.DTO.DTO_detail_review;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_detail_image extends Fragment {
    String shopNm;
    String TAG = "fragment_detail_image";
    RecyclerView recyclerView;
    adapter_detail_image adapterDetailImage;
    home_coffee_service coffeeAPI;
    ProgressDialog loadingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_image, container, false);

        loadingDialog = ProgressDialog.show(getContext(), "로딩중 ...", "Please Wait...", true, false);

        Intent intent = getActivity().getIntent();
        int seq = intent.getIntExtra("seq", 0);
        shopNm = intent.getStringExtra("name");

        recyclerView = (RecyclerView) view.findViewById(R.id.detail_image_recyclerView);
        coffeeAPI = useItem.getRetrofit().create(home_coffee_service.class);

        getImage(seq);

        loadingDialog.dismiss();

        return view;
    }

    private void getImage(int seq) {
        coffeeAPI.getDetailImage(seq, 1).enqueue(new Callback<List<DTO_detail_review>>() {
            @Override
            public void onResponse(Call<List<DTO_detail_review>> call, Response<List<DTO_detail_review>> response) {
                if(response.isSuccessful()) {
                    List<DTO_detail_review> imageList = response.body();
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    adapterDetailImage = new adapter_detail_image(imageList, shopNm);
                    recyclerView.setAdapter(adapterDetailImage);

                }
            }

            @Override
            public void onFailure(Call<List<DTO_detail_review>> call, Throwable t) {

            }
        });
    }
}