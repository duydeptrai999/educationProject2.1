package com.duyth10.dellhieukieugiservice.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.duyth10.dellhieukieugiservice.DataProcessingService;
import com.duyth10.dellhieukieugiservice.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] permissions = {
            Manifest.permission.CAMERA
    };

    private DataProcessingService mService;
    private boolean mBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataProcessingService.LocalBinder binder = (DataProcessingService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            // Nhận dữ liệu đã xử lý từ dịch vụ
            String processedData = mService.processReceivedData();
            Log.d("MainActivity", "Received processed data: " + processedData);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra quyền
        checkPermission();

        // Xử lý intent được truyền vào
//        Intent intent = getIntent();
        handleIntent(getIntent());

        // Cài đặt màu cho thanh trạng thái
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));

        // Thêm fragment nếu chưa có
        if (savedInstanceState == null) {
            QRFragment exampleFragment = new QRFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, exampleFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu từ dịch vụ
        reloadServiceData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent !=  null) {
            String textFromMain = intent.getStringExtra("data");
            int statusBarColor = intent.getIntExtra("statusBarColor", ContextCompat.getColor(this, R.color.lavender));
            int toolbarColor = intent.getIntExtra("toolbarColor", ContextCompat.getColor(this, R.color.lavender));

            Log.d("MainActivityyyy", "Received textFromMain: " + textFromMain);
            Log.d("MainActivityyyy", "Received statusBarColor: " + statusBarColor);
            Log.d("MainActivityyyy", "Received toolbarColor: " + toolbarColor);

            // Khởi động service và truyền dữ liệu vào
            Intent serviceIntent = new Intent();

            serviceIntent.setComponent(new ComponentName("com.duyth10.dellhieukieugiservice", "com.duyth10.dellhieukieugiservice.DataProcessingService"));
            serviceIntent.putExtra("dataMain", textFromMain);
            serviceIntent.putExtra("statusBarColor", statusBarColor);
            serviceIntent.putExtra("toolbarColor", toolbarColor);
            startService(serviceIntent);
        }
    }

    private void reloadServiceData() {
        if (mBound) {
            // Lấy lại dữ liệu đã xử lý từ dịch vụ
            String processedData = mService.processReceivedData();
            Log.d("MainActivity", "Reloaded data: " + processedData);
            // Cập nhật UI hoặc xử lý dữ liệu nếu cần
        }
    }
//  will take first intent sent
    private void handleIntent(Intent intent) {
        if (intent != null) {
            // Lấy dữ liệu từ Intent
            String textFromMain = intent.getStringExtra("data");
            int statusBarColor = intent.getIntExtra("statusBarColor", ContextCompat.getColor(this, R.color.lavender));
            int toolbarColor = intent.getIntExtra("toolbarColor", ContextCompat.getColor(this, R.color.lavender));

            Log.d("MainActivityyyy", "Received textFromMain: " + textFromMain);
            Log.d("MainActivityyyy", "Received statusBarColor: " + statusBarColor);
            Log.d("MainActivityyyy", "Received toolbarColor: " + toolbarColor);

            // Khởi động service và truyền dữ liệu vào
            Intent serviceIntent = new Intent();
            serviceIntent.setComponent(new ComponentName("com.duyth10.dellhieukieugiservice", "com.duyth10.dellhieukieugiservice.DataProcessingService"));
            serviceIntent.putExtra("dataMain", textFromMain);
            serviceIntent.putExtra("statusBarColor", statusBarColor);
            serviceIntent.putExtra("toolbarColor", toolbarColor);
            startService(serviceIntent);

            // Kiểm tra và xử lý dữ liệu nếu không null
            if (textFromMain != null) {
                Log.d("MainActivityyyy", "Received textFromMain: " + textFromMain);
            } else {
                Log.d("MainActivityyyy", "Intent received but no data found.");
            }
        }
    }

    private void checkPermission() {
        // Danh sách lưu các quyền chưa được cấp
        List<String> permissionsToRequest = new ArrayList<>();

        // Kiểm tra từng quyền
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            // Chuyển danh sách các quyền chưa được cấp sang mảng
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            // Yêu cầu những quyền chưa được cấp
            ActivityCompat.requestPermissions(this, permissionsArray, 1);
        }
    }
}
