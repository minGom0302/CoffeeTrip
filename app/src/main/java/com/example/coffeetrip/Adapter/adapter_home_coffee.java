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

import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.R;

import java.util.List;

public class adapter_home_coffee extends RecyclerView.Adapter<adapter_home_coffee.MyViewHolder> {
    private final String TAG = "adapter_home_coffee : ";
    private List<DTO_home_coffee> DTO_list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView shopNm;
        private TextView shopLoca;

        public MyViewHolder(@NonNull View view) {
            super(view);

            image = view.findViewById(R.id.listview_main_home_coffee_backImage);
            shopNm = view.findViewById(R.id.listview_main_home_coffee_name);
            shopLoca = view.findViewById(R.id.listview_main_home_coffee_loca);
        }
    }

    public adapter_home_coffee(List<DTO_home_coffee> DTO_list) {
        Log.i(TAG, "생성자 만듬");
        this. DTO_list = DTO_list;
    };

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public adapter_home_coffee.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.listview_main_home_coffee, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder 들어옴 : " + DTO_list);

        DTO_home_coffee item = DTO_list.get(position);

        holder.image.setImageResource(R.drawable.icon_cafe);
        holder.shopNm.setText(item.getNm());
        holder.shopLoca.setText(item.loca + " " + item.gu);
    }

    @Override
    public int getItemCount() {
        return DTO_list.size();
    }
}
