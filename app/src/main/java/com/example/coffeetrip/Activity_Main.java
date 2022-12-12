package com.example.coffeetrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.coffeetrip.handler.backspaceHandler;
import com.example.coffeetrip.use.useItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Activity_Main extends AppCompatActivity {
    private static final String TAG = "Main_Activity";
    private backspaceHandler bsHandler = new backspaceHandler(this);
    private BottomNavigationView botNavView;
    Fragment_main_home f01 = null;
    Fragment_main_magnifier f02 = null;
    Fragment_main_favorite f03 = null;
    Fragment_main_mypage f04 = null;

    static final int PERMISSIONS_REQUEST = 0x0000001;
    useItem item = new useItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        item.OnCheckPermission(this, this);
        //OnCheckPermission();

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
                            f03.setRecyclerView();
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

    // 권한 요청 응답 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 요청 수락했을 때
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정되었습니다.", Toast.LENGTH_LONG).show();
                } else {
                    // 권한 요청 거절했을 때
                    item.showDialog(this, this, "권한 확인", "권한을 허용하지 않으면 종료됩니다.\n해당 앱에 대한 권한을 다시 설정하시겠습니까?", "확인");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        bsHandler.onBackPressed();
    }
}
