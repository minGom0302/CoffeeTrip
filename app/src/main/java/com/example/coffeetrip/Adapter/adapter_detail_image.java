package com.example.coffeetrip.Adapter;

import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.Activity_ImageView;
import com.example.coffeetrip.DTO.DTO_detail_review;
import com.example.coffeetrip.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class adapter_detail_image extends RecyclerView.Adapter<adapter_detail_image.MyViewHolder> {
    List<DTO_detail_review> imageList;
    Context context;
    String shopNm;

    public adapter_detail_image(List<DTO_detail_review> imageList, String shopNm) {
        this.imageList = imageList;
        this.shopNm = shopNm;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.listview_detail_image_imageView);
        }
    }

    @NonNull
    @Override
    public adapter_detail_image.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_detail_image, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_detail_image.MyViewHolder holder, int position) {
        DTO_detail_review dto = imageList.get(position);
        String nickName;
        String review;
        String date;
        String fileName = dto.getImageName();
        String url =  "http://119.148.144.244:9172/image/image/";

        Glide.with(context).load(url+fileName).into(holder.imageView);
        holder.imageView.setImageResource(R.drawable.som1);

        if(!(dto.getDate().length() < 1)) {
            SimpleDateFormat input = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat output = new SimpleDateFormat("yyyy.MM.dd");
            Date dt = null;
            try {
                dt = input.parse(dto.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date = output.format(dt);
            nickName = dto.getNickName();
            review = dto.getReview();
        } else {
            date = "";
            nickName = shopNm;
            review = "업체용 사진";
        }


        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Activity_ImageView.class);
            intent.putExtra("imageName", fileName);
            intent.putExtra("nickName", nickName);
            intent.putExtra("review", review);
            intent.putExtra("date", date);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return imageList.size(); }
}
