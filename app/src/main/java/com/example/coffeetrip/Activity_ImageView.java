package com.example.coffeetrip;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class Activity_ImageView extends AppCompatActivity {
    ImageView imageView;
    TextView nickNameTv, reviewTv, dateTv;

    String[] items = {"사진 다운로드", "이미지 공유"};
    String nickName, review, date, imageName;
    String url = "http://119.148.144.244:9172/image/image/";
    // 다운로드 중일 때 보여질 화면면
    ProgressDialog loadingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        // 앱 바 없애기
        getSupportActionBar().hide();

        imageView = (ImageView) findViewById(R.id.imageView_imageview);
        nickNameTv = (TextView) findViewById(R.id.imageView_nickNameTv);
        dateTv = (TextView) findViewById(R.id.imageView_dateTv);
        reviewTv = (TextView) findViewById(R.id.imageView_reviewTv);

        Intent intent = getIntent();
        imageName = intent.getStringExtra("imageName");
        nickName = intent.getStringExtra("nickName");
        review = intent.getStringExtra("review");
        date = intent.getStringExtra("date");

        // setText 에서 \n 을 인식하지 못하고 표기가 되기 때문에 replace 를 사용해서 라인 변경을 해준다.
        reviewTv.setText(review.replace("\\n", Objects.requireNonNull(System.getProperty("line.separator"))));
        nickNameTv.setText(nickName);
        dateTv.setText(date);
        Glide.with(this).load(url + imageName).into(imageView);

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showList();
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(completeReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 브로드캐스트 리시버 등록
        // ACTION_DOWNLOAD_COMPLETE : 다운로드가 완료되었을 때 전달
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter);
    }

    // AlterDialog 띄움 > 사진 다운 혹은 사진 공유
    private void showList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageEvent(which);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // showList에서 선택한 값에 따라 실행
    private void imageEvent(int num) {
        switch (num) {
            case 0: // 사진다운로드
                downLoad();
                Toast.makeText(this, "사진 다운로드 준비중입니다.", Toast.LENGTH_SHORT).show();
                break;
            case 1: // 사진공유
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                String msg = "링크를 준비중입니다.";
                intentShare.addCategory(Intent.CATEGORY_DEFAULT);
                intentShare.putExtra(Intent.EXTRA_TEXT, msg);
                intentShare.setType("text/plain");
                startActivity(Intent.createChooser(intentShare, "앱을 선택해주세요."));
                break;
        }
    }

    // 사진 다운로드 실행하는 메소드 with Download Manager
    protected void downLoad() {
        Uri uri = Uri.parse(url + imageName);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription("이미지 다운");
        request.setTitle("이미지 타이틀");
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, imageName);
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
        final long downloadId = manager.enqueue(request);

        // 화면에 띄워줄 로딩 다이아로그 실행
        loadingDialog = ProgressDialog.show(Activity_ImageView.this, "이미지 다운로드 중 ...", "Please Wait...", true, false);

        new Thread(new Runnable() {
            @SuppressLint("Range")
            @Override
            public void run() {
                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);

                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    cursor.close();
                }
            }
        }).start();
    }

    // 다운로드가 완료되었을 때 처리를 하기 위한 브로드캐스트
    private BroadcastReceiver completeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 화면에 띄워줄 로딩 다이아로그 숨김 (완료 처리)
            loadingDialog.dismiss();
            Toast.makeText(context, "파일 다운로드를 완료했습니다.", Toast.LENGTH_SHORT).show();
        }
    };
}