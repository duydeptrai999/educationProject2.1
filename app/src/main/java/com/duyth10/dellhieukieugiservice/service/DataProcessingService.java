package com.duyth10.dellhieukieugiservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.duyth10.dellhieukieugiservice.R;

import com.duyth10.dellhieukieugiservice.IMyAidlInterface;


public class DataProcessingService extends Service {

    // Tạo binder để kết nối giữa các service và các client
//    private final IBinder binder = new LocalBinder();
    private String receiverData;

    private int statusBarColor;
    private int toolbarColor;

    public DataProcessingService() {}

    // Binder cung cấp cơ chế giao tiếp giữa các tiến trình khác nhau giữa các thành phần trong app hoặc app khác
    // Giúp kết nối client và service
//    public class LocalBinder extends Binder {
//        // Trả về đối tượng Service để Client có thể gọi các phương thức công khai
//        public DataProcessingService getService() {
//            return DataProcessingService.this;
//        }
//    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DataProcessingService", "Service Created");
    }
    private final IMyAidlInterface.Stub aidlBinder = new IMyAidlInterface.Stub() {
        @Override
        public void sendData(String data) throws RemoteException {
            Log.d("DataProcessingService", "Received data: " + data);
            receiverData = data;

        }
    };

    // onBind dùng để kết nối giữa các thành phần trong app
    // hoặc app khác keets nối bằng aidl
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DataProcessingService", "on binder with intent :"+intent);
        return aidlBinder; // Trả về AIDL Binder để Client App sử dụng
    }


    // onStartCommand() được gọi khi một Client khởi động Service bằng startService()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DataProcessingService", "on start command");

        if (intent != null) {
            receiverData = intent.getStringExtra("dataMain");
            Log.d("DataProcessingService", "Received data: " + receiverData);

            statusBarColor = intent.getIntExtra("statusBarColor", ContextCompat.getColor(this, R.color.lavender));
            toolbarColor = intent.getIntExtra("toolbarColor", ContextCompat.getColor(this, R.color.lavender));

            Log.d("DataProcessingService", "Received statusBarColor: " + statusBarColor);
            Log.d("DataProcessingService", "Received toolbarColor: " + toolbarColor);
        } else {
            Log.d("DataProcessingService", "Intent is null");
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("DataProcessingService", "Service Unbound");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DataProcessingService", "Service Destroyed");
    }

//    public String processReceivedData() {
//        // Thực hiện xử lý dữ liệu
//        return receiverData;
//    }
//
//    public int processReceivedDataColorStatus() {
//        // Thực hiện xử lý màu status bar
//        return statusBarColor;
//    }
//
//    public int processReceivedDataColorToolbar() {
//        // Thực hiện xử lý màu toolbar
//        return toolbarColor;
//    }


}
