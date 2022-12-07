package com.example.coffeetrip.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.coffeetrip.DTO.DTO_detail_image;
import com.example.coffeetrip.R;

import java.util.List;

public class adapter_detail_image extends RecyclerView.Adapter<adapter_detail_image.MyViewHolder> {
    List<DTO_detail_image> imageList;
    Context context;

    public adapter_detail_image(List<DTO_detail_image> imageList) {
        this.imageList = imageList;
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
        DTO_detail_image dto = imageList.get(position);
        String fileName = dto.getFileName();
        String url =  "http://119.148.144.244:9172/image/image/";
        Glide.with(context).load(url+fileName).into(holder.imageView);
        holder.imageView.setImageResource(R.drawable.som1);
    }

    @Override
    public int getItemCount() { return imageList.size(); }
}
