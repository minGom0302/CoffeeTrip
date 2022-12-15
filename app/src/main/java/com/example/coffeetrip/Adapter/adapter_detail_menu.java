package com.example.coffeetrip.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.DTO.DTO_detail_menu;
import com.example.coffeetrip.R;

import java.util.List;

public class adapter_detail_menu extends RecyclerView.Adapter<adapter_detail_menu.MyViewHolder> {
    List<DTO_detail_menu> menuList;
    Context context;

    public adapter_detail_menu(List<DTO_detail_menu> menuList) {
        this.menuList = menuList;
        Log.i("menuList", String.valueOf(menuList));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView menuNm, price;

        public MyViewHolder(@NonNull View view) {
            super(view);

            image = view.findViewById(R.id.listview_detail_menu_image);
            menuNm = view.findViewById(R.id.listview_detail_menu_name);
            price = view.findViewById(R.id.listview_detail_menu_price);
        }
    }

    @NonNull
    @Override
    public adapter_detail_menu.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_detail_menu, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DTO_detail_menu menu = menuList.get(position);

        holder.menuNm.setText(menu.menuName);
        holder.price.setText(menu.menuPrice + "원");

        if(menu.imagePath.length() < 1) {
            holder.image.setImageResource(R.drawable.som1);
        } else {
            String url = "http://119.148.144.244:9172/image/menu/";
            Glide.with(context).load(url+menu.getImageName()).placeholder(R.drawable.som1).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
}
