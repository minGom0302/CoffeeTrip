package com.example.coffeetrip;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.coffeetrip.Adapter.adapter_detail_review;
import com.example.coffeetrip.DTO.DTO_detail_review;
import com.example.coffeetrip.Interface.home_coffee_service;
import com.example.coffeetrip.use.useItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_main_mypage extends Fragment {
    TextView nicknameTv, reviewCntTv;
    ImageButton editingBtn;
    RecyclerView recyclerView;
    AppCompatButton logoutBtn;

    SharedPreferences sp;
    SharedPreferences.Editor sp_e;
    home_coffee_service API;
    adapter_detail_review reviewAdapter;

    private static final int MODIFY_INFO_ACTIVITY = 10000;
    String nm, nickname, address, phone, id;

    @Override
    public void onResume() {
        super.onResume();
        settingLayout();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_mypage, container, false);

        API = useItem.getRetrofit().create(home_coffee_service.class);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        sp_e = sp.edit();
        nm = sp.getString("nm", null);
        nickname = sp.getString("nickname", null);
        address = sp.getString("address", null);
        phone = sp.getString("phone", null);
        id = sp.getString("id", null);

        nicknameTv = (TextView) view.findViewById(R.id.myPage_nicknameTv);
        reviewCntTv = (TextView) view.findViewById(R.id.myPage_reviewCntTv);
        editingBtn = (ImageButton) view.findViewById(R.id.myPage_editingBtn);
        recyclerView = (RecyclerView) view.findViewById(R.id.myPage_recyclerView);
        logoutBtn = view.findViewById(R.id.myPage_logoutBtn);

        editingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Activity_ModifyInfo.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, MODIFY_INFO_ACTIVITY);
        });

        logoutBtn.setOnClickListener(v -> {
            logoutCheck();
        });

        settingLayout();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == MODIFY_INFO_ACTIVITY) {
            if(resultCode == RESULT_OK) {
                String nick = intent.getExtras().getString("nick");
                if(nick != null) {
                    nickname = nick;
                    settingLayout();
                }
            }
        }
    }


    private void settingLayout() {
        nicknameTv.setText(nickname);
        API.getMyReview(id).enqueue(new Callback<List<DTO_detail_review>>() {
            @Override
            public void onResponse(Call<List<DTO_detail_review>> call, Response<List<DTO_detail_review>> response) {
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        List<DTO_detail_review> reviewList = response.body();
                        reviewCntTv.setText(String.valueOf(reviewList.size()));
                        reviewAdapter = new adapter_detail_review(reviewList, nickname);
                        reviewAdapter.setOnItemClickLayout(new adapter_detail_review.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position, DTO_detail_review dto) {
                                Intent intent = new Intent(getContext(), Activity_DetailPage.class);
                                intent.putExtra("seq", dto.getShopSeq());
                                intent.putExtra("name", dto.getShopNm());
                                startActivity(intent);
                            }
                        });
                        reviewAdapter.setOnItemClickListener(new adapter_detail_review.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position, DTO_detail_review dto) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("안내").setMessage("해당 리뷰를 삭제하시겠습니까?");
                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        API.deleteReview(dto.getSeq()).enqueue(new Callback<Integer>() {
                                            @Override
                                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                                if (response.isSuccessful()) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                    builder.setTitle("안내").setMessage("리뷰가 삭제되었습니다.");
                                                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            settingLayout();
                                                        }
                                                    });
                                                    AlertDialog alertDialog = builder.create();
                                                    alertDialog.show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Integer> call, Throwable t) {

                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        recyclerView.setAdapter(reviewAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DTO_detail_review>> call, Throwable t) {

            }
        });
    }

    private void logoutCheck() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("안내").setMessage("로그아웃을 하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp_e.putBoolean("autoLoginCb", false);
                sp_e.commit();

                Intent intent = new Intent(getActivity(), Activity_Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}