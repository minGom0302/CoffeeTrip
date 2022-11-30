package com.example.coffeetrip.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.R;

import java.util.ArrayList;

public class adapter_multi_image extends RecyclerView.Adapter<adapter_multi_image.ViewHolder> {
    private ArrayList<Uri> mData = null;
    private Context mContext = null;

    // 생성자
    public adapter_multi_image (ArrayList<Uri> list, Context context) {
        mData = list;
        mContext = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_image_upload, parent, false);
        adapter_multi_image.ViewHolder vh = new adapter_multi_image.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri image_uri = mData.get(position);

        Glide.with(mContext).load(image_uri).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clearItems() { mData.clear(); }
}
