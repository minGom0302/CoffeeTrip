package com.example.coffeetrip.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class adapter_detail_review extends RecyclerView.Adapter<adapter_detail_review.MyViewHolder> {
    List<DTO_detail_review> reviewList;
    Context context;

    public adapter_detail_review(List<DTO_detail_review> reviewList) {
        this.reviewList = reviewList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nickName;
        private TextView review;
        private TextView rating;
        private TextView date;
        private RatingBar ratingBar;
        private ImageView imageView;

        public MyViewHolder(@NonNull View view) {
            super(view);

            nickName = (TextView) view.findViewById(R.id.listview_detail_review_nickname);
            review = (TextView) view.findViewById(R.id.listview_detail_review_review);
            rating = (TextView) view.findViewById(R.id.listview_detail_review_rating);
            date = (TextView) view.findViewById(R.id.listview_detail_review_date);
            ratingBar = (RatingBar) view.findViewById(R.id.listview_detail_review_ratingbar);
            imageView = (ImageView) view.findViewById(R.id.listview_detail_review_imageView);

        }
    }

    @NonNull
    @Override
    public adapter_detail_review.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_detail_review, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DTO_detail_review reviewDTO = reviewList.get(position);

        SimpleDateFormat input = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat output = new SimpleDateFormat("yyyy.MM.dd");
        Date dt = null;
        try {
            dt = input.parse(reviewDTO.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String date = output.format(dt);

        holder.nickName.setText(reviewDTO.getNickName());
        holder.review.setText(reviewDTO.getReview());
        holder.date.setText(date);
        holder.ratingBar.setRating(reviewDTO.getRating());

        if(reviewDTO.getRating() % 1 == 0) {
            holder.rating.setText(String.valueOf((int)reviewDTO.getRating()));
        } else {
            holder.rating.setText(String.valueOf(reviewDTO.getRating()));
        }
        Log.i("imageName", reviewDTO.getImageName());
        if(!reviewDTO.getImageName().isEmpty()) {
            String url = "http://119.148.144.244:9172/image/image/";
            Glide.with(context).load(url + reviewDTO.getImageName()).into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Activity_ImageView.class);
                intent.putExtra("imageName", reviewDTO.getImageName());
                intent.putExtra("nickName", reviewDTO.getNickName());
                intent.putExtra("review", reviewDTO.getReview());
                intent.putExtra("date", date);
                context.startActivity(intent);
            });
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}