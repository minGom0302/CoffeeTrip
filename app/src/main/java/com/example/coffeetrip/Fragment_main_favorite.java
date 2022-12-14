package com.example.coffeetrip;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeetrip.Adapter.adapter_main_favorite;
import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_main_favorite extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView favoriteRecyclerView;
    adapter_main_favorite favoriteAdapter;
    home_coffee_service API;
    SharedPreferences sp;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog loadingDialog;
    TextView memoTv;

    String id;
    int recentlyCount = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_favorite, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        id = sp.getString("id", null);
        API = useItem.getRetrofit().create(home_coffee_service.class);
        memoTv = (TextView) view.findViewById(R.id.main_favorite_memoTv);
        favoriteRecyclerView = (RecyclerView) view.findViewById(R.id.main_favorite_recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_favorite_swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setDistanceToTriggerSync(200);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Intent ??? ???????????? ????????? ????????? ???????????? ??? RecyclerView refresh
        setRecyclerView();
    }

    public void setRecyclerView() {
        API.getFavorite(id).enqueue(new Callback<List<DTO_home_coffee>>() {
            @Override
            public void onResponse(Call<List<DTO_home_coffee>> call, Response<List<DTO_home_coffee>> response) {
                if(response.isSuccessful()) {
                    List<DTO_home_coffee> dtoList = response.body();

                    if(dtoList.size() > 0) {
                        memoTv.setVisibility(View.INVISIBLE);
                    } else {
                        memoTv.setVisibility(View.VISIBLE);
                    }

                    if(recentlyCount != dtoList.size()) {
                        favoriteAdapter = new adapter_main_favorite(dtoList, id);
                        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

                        favoriteAdapter.setOnItemClickListener(new adapter_main_favorite.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position, DTO_home_coffee dto) {
                                // ????????? ?????????
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("????????? ??????").setMessage("?????? ????????? ???????????? ?????????????????????????\n???????????? ?????? ???????????? ???????????????.");
                                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // ???????????? ??????
                                        loadingDialog = ProgressDialog.show(getContext(), "???????????? ????????? ...", "Please Wait...", true, false);
                                        home_coffee_service API = useItem.getRetrofit().create(home_coffee_service.class);
                                        API.minusFavorite(dto.getSeq(), id).enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                Toast.makeText(getContext(), "????????? / ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                                setRecyclerView();
                                                loadingDialog.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {

                                            }
                                        });
                                    }
                                });

                                builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });

                        favoriteRecyclerView.setAdapter(favoriteAdapter);
                        recentlyCount = dtoList.size();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DTO_home_coffee>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRefresh() {
        recentlyCount = 0;
        setRecyclerView();
        swipeRefreshLayout.setRefreshing(false);
    }
}