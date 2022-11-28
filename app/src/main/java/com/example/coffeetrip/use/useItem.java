package com.example.coffeetrip.use;

import android.content.Context;
import android.widget.Toast;

public class useItem {

    // 일반 토스트메세지 뿌리기
    public void toastMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
