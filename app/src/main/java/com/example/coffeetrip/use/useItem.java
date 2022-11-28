package com.example.coffeetrip.use;

import android.content.Context;
import android.widget.Toast;

public class useItem {
    public void toastMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
