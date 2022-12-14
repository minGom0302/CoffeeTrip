package com.example.coffeetrip.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeetrip.DTO.DTO_home_coffee;
import com.example.coffeetrip.R;
import com.example.coffeetrip.use.useItem;

import java.util.List;

public class adapter_home_coffee extends RecyclerView.Adapter<adapter_home_coffee.MyViewHolder> {
    private final String TAG = "adapter_home_coffee : ";
    private List<DTO_home_coffee> DTO_list;
    Context context;

    // 외부에서 클릭이벤트 사용을 위해 언터페이스 작성
    public interface OnItemClickListener {
        void onItemClick(View view, int position, DTO_home_coffee dto);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mlistener = null;

    // OnItemClickListener 리스너 객체 참조를 아답터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView shopNm;
        private TextView shopLoca;
        private TextView favoriteCount;

        public MyViewHolder(@NonNull View view) {
            super(view);

            image = view.findViewById(R.id.listview_main_home_coffee_backImage);
            shopNm = view.findViewById(R.id.listview_main_home_coffee_name);
            shopLoca = view.findViewById(R.id.listview_main_home_coffee_loca);
            favoriteCount = view.findViewById(R.id.listview_main_home_coffee_favoriteCount);

            // item 클릭 이벤트 처리
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // getAdapterPosition : 현재 자신의 위치를 알아내는 메소드
                    int position = getAdapterPosition();
                    // 리사이클러뷰가 아이템뷰를 갱신하는 과정에서 아이템이 어댑터에서 삭제되면 getAdapterPosition() 메서드는 NO_POSITION을 리턴하기 때문에 체크를 해준다.
                    if (position != RecyclerView.NO_POSITION) {
                        if(mlistener != null) {
                            // 리스너 객체의 메서드 호출
                            DTO_home_coffee dto = DTO_list.get(position);
                            mlistener.onItemClick(view, position, dto);
                        }
                    }
                }
            });
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
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.listview_main_home_coffee, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder 들어옴 : " + DTO_list);

        DTO_home_coffee item = DTO_list.get(position);

        holder.shopNm.setText(item.getNm());
        holder.shopLoca.setText(item.loca + " " + item.gu);
        holder.favoriteCount.setText("좋아요 : " + useItem.createComma(item.favorite));

        String url = "http://119.148.144.244:9172/image/title/";
        if(item.getImageName() == null) {
            holder.image.setImageResource(R.drawable.icon_cafe);
        } else {
            Glide.with(context).load(url+item.getImageName()).placeholder(R.drawable.som1).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return DTO_list.size();
    }
}
