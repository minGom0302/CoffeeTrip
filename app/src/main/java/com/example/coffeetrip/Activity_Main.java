package com.example.coffeetrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.coffeetrip.handler.backspaceHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Activity_Main extends AppCompatActivity {
    private static final String TAG = "Main_Activity";
    private backspaceHandler bsHandler = new backspaceHandler(this);
    private BottomNavigationView botNavView;
    Fragment_main_home f01 = null;
    Fragment_main_magnifier f02 = null;
    Fragment_main_favorite f03 = null;
    Fragment_main_mypage f04 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botNavView = findViewById(R.id.main_bottom_navigation);

        // 첫 화면으로 fragment_main_home.xml 띄움
        f01 = new Fragment_main_home();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_container, f01).commit();

        // Bot Menu 선택에 따른 Fragment 화면 띄움, 화면 선택에 따라 구현된게 남아있게 할려고 이렇게 구성함
        botNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.main_bottom01 :
                        if (f01 == null) {
                            f01 = new Fragment_main_home();
                            getSupportFragmentManager().beginTransaction().add(R.id.main_frame_container, f01).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().show(f01).commit();
                        }
                        if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                        if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                        if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                        // getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_container, new Fragment_main_home()).commit();
                        break;
                    case R.id.main_bottom02 :
                        if(f02 == null) {
                            Log.i(TAG, "f02 null");
                            f02 = new Fragment_main_magnifier();
                            getSupportFragmentManager().beginTransaction().add(R.id.main_frame_container, f02).commit();
                        } else {
                            Log.i(TAG, "f02 not null");
                            getSupportFragmentManager().beginTransaction().show(f02).commit();
                        }
                        if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                        if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                        if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                        break;
                    case R.id.main_bottom03 :
                        if(f03 == null) {
                            f03 = new Fragment_main_favorite();
                            getSupportFragmentManager().beginTransaction().add(R.id.main_frame_container, f03).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().show(f03).commit();
                        }
                        if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                        if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                        if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                        break;
                    case R.id.main_bottom04 :
                        if(f04 == null) {
                            f04 = new Fragment_main_mypage();
                            getSupportFragmentManager().beginTransaction().add(R.id.main_frame_container, f04).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().show(f04).commit();
                        }
                        if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                        if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                        if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                        break;
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
