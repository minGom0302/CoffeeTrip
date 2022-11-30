package com.example.coffeetrip.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.coffeetrip.R;

import java.util.List;

public class adapter_spinner extends BaseAdapter {
    private final List<String> list;
    private final LayoutInflater inflater;
    private String text;

    public adapter_spinner (Context context, List<String> list) {
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 화면에 들어왔을 때 보여지는 텍스트뷰 설정
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.spinner_text, parent, false);
        if (list != null) {
            text = list.get(position);
            ((TextView) convertView.findViewById(R.id.spinner_inner_text)).setText(text);
        }
        return convertView;
    }
}
