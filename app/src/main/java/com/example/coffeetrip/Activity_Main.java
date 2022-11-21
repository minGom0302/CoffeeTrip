package com.example.coffeetrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.coffeetrip.handler.backspaceHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Activity_Main extends AppCompatActivity {
    private static final String TAG = "Main_Activity";
    private backspaceHandler bsHandler = new backspaceHandler(this);
    private BottomNavigationView botNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botNavView = findViewById(R.id.main_bottom_navigation);

        // 첫 화면으로 fragment_main_home.xml 띄움
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_container, new Fragment_main_home()).commit();

        // Bot Menu 선택에 따른 Fragment 화면 띄움움
       botNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.main_bottom01 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_container, new Fragment_main_home()).commit();
                        break;
                    case R.id.main_bottom02 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_container, new Fragment_main_magnifier()).commit();
                        break;
                    case R.id.main_bottom03 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_container, new Fragment_main_favorite()).commit();
                        break;
                    case R.id.main_bottom04 :
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_container, new Fragment_main_mypage()).commit();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        bsHandler.onBackPressed();
    }
}
