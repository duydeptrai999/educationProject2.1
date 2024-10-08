package com.duyth10.dellhieukieugiservice.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
<<<<<<< HEAD
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
=======
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b

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

<<<<<<< HEAD
=======

>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b
    private DataProcessingService mService;
    private boolean mBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataProcessingService.LocalBinder binder = (DataProcessingService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

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


        checkPermission();


        Intent intent = getIntent();
        handleIntent(intent);

<<<<<<< HEAD
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));
=======
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b

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


    private void reloadServiceData() {
        if (mBound) {
            String processedData = mService.processReceivedData();
            Log.d("MainActivity", "Reloaded data: " + processedData);
<<<<<<< HEAD
=======
            // Cập nhật UI hoặc xử lý dữ liệu
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b
        }
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            // Lấy dữ liệu từ Intent
            //     String qrData = intent.getStringExtra("qrData");
            String textFromMain = intent.getStringExtra("data");
<<<<<<< HEAD
            int statusBarColor = intent.getIntExtra("statusBarColor", ContextCompat.getColor(this, R.color.lavender));
            int toolbarColor = intent.getIntExtra("toolbarColor", ContextCompat.getColor(this, R.color.lavender));


            //      Log.d("MainActivityyyy", "Received qrData: " + qrData);
            Log.d("MainActivityyyy", "Received textFromMain: " + textFromMain);
            Log.d("MainActivityyyy", "Received textFromMain1: " + statusBarColor);
            Log.d("MainActivityyyy", "Received textFromMain2: " + toolbarColor);
=======

            //      Log.d("MainActivityyyy", "Received qrData: " + qrData);
            Log.d("MainActivityyyy", "Received textFromMain: " + textFromMain);
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b

            Intent serviceIntent = new Intent();
            serviceIntent.setComponent(new ComponentName("com.duyth10.dellhieukieugiservice", "com.duyth10.dellhieukieugiservice.DataProcessingService"));
            serviceIntent.putExtra("dataMain", textFromMain);
<<<<<<< HEAD
            serviceIntent.putExtra("statusBarColor", statusBarColor);
            serviceIntent.putExtra("toolbarColor", toolbarColor);
=======
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b
            startService(serviceIntent);

            // Kiểm tra và xử lý dữ liệu nếu không null
            if (textFromMain != null) {
                // Ví dụ: Hiển thị dữ liệu lên màn hình hoặc xử lý logic
                //       Log.d("MainActivityyyy", "Received qrData: " + qrData);
                Log.d("MainActivityyyy", "Received textFromMain: " + textFromMain);
            } else {
                Log.d("MainActivityyyy", "Intent received but no data found.");
            }
        }
    }

    private void checkPermission() {

<<<<<<< HEAD
        List<String> permissionsToRequest = new ArrayList<>();

=======
        // Danh sách lưu các quyền chưa được cấp
        List<String> permissionsToRequest = new ArrayList<>();

        // Kiểm tra từng quyền
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
<<<<<<< HEAD
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
=======
            // Chuyển danh sách các quyền chưa được cấp sang mảng
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            // Yêu cầu những quyền chưa được cấp
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b
            ActivityCompat.requestPermissions(this, permissionsArray, 1);
        } else {


        }
    }
}
