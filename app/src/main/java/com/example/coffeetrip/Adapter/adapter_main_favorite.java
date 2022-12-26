package com.example.coffeetrip.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.Activity_DetailPage;
import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.R;

import java.util.List;


public class adapter_main_favorite extends RecyclerView.Adapter<adapter_main_favorite.MyViewHolder> {
    private List<DTO_home_coffee> DTO_list;
    private Context context;
    private String id;
    private ProgressDialog loadingDialog;
    private OnItemClickListener mlistener = null;

    public adapter_main_favorite(List<DTO_home_coffee> DTO_list, String id) {
        this.DTO_list = DTO_list;
        this.id = id;
    }

    public interface  OnItemClickListener {
        void onItemClick(View view, int position, DTO_home_coffee dto);
    }

    public void setOnItemClickListener(OnItemClickListener listener) { mlistener = listener; }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView shopNm;
        private TextView address;
        private ImageButton favoriteBtn;
        private LinearLayout linearLayout;
        private RatingBar ratingBar;

        public MyViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.listview_main_favorite_imageView);
            shopNm = view.findViewById(R.id.listview_main_favorite_nmTv);
            address = view.findViewById(R.id.listview_main_favorite_addressTv);
            favoriteBtn = view.findViewById(R.id.listview_main_favorite_favoriteBtn);
            linearLayout = view.findViewById(R.id.listview_main_favorite_linearLayout);
            ratingBar = view.findViewById(R.id.listview_main_favorite_ratingBar);

            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        if(mlistener != null) {
                            DTO_home_coffee dto = DTO_list.get(position);
                            mlistener.onItemClick(view, position, dto);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.listview_home_favorite, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String url = "http://MyServerAddress/image/title/";

        DTO_home_coffee item = DTO_list.get(position);
        holder.shopNm.setText(item.getNm());
        holder.address.setText(item.getAddress());
        holder.ratingBar.setRating(item.getRating());

        if(item.getImageName() == null) {
            holder.imageView.setImageResource(R.drawable.icon_cafe);
        } else {
            Glide.with(context).load(url+item.getImageName()).placeholder(R.drawable.som1).into(holder.imageView);
        }

        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Activity_DetailPage.class);
            intent.putExtra("seq", item.getSeq());
            intent.putExtra("name", item.getNm());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return DTO_list.size(); }
}
