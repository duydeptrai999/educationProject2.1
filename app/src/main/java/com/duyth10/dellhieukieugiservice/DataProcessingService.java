package com.duyth10.dellhieukieugiservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class DataProcessingService extends Service {

    // tạo binder để kết nối giữa các service và các client
    private final IBinder binder = new LocalBinder();
    private  String reciverData;

    private int statusBarColor;

    private int toolbarColor;


    public DataProcessingService() {
    }

    //binder cung cấp cơ chế giao tiếp giữa các tiến trình khác nhau giữa các thành phần trong app hoặc app khác
    // giúp kết nối client và service
    public class LocalBinder extends Binder {
        //Trả về đối tượng Service để Client có thể gọi các phương thức công khai
        public DataProcessingService getService() {
            return DataProcessingService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("app2", "Service Created");
    }


    // onbind dùng để kết nối giữa các thành phần trong app
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    // onStartCommand() được gọi khi một Client khởi động Service bằng startService()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("app2","on start command");

        if (intent != null) {
            reciverData = intent.getStringExtra("dataMain");
            Log.d("DataProcessingService", "Received data: " + reciverData);

  //          String qrData = intent.getStringExtra("qrData");
 //           String textFromMain = intent.getStringExtra("data");
             statusBarColor = intent.getIntExtra("statusBarColor", ContextCompat.getColor(this, R.color.lavender));
             toolbarColor = intent.getIntExtra("toolbarColor", ContextCompat.getColor(this, R.color.lavender));

            //       Log.d("DataProcessingService", "Received qrData: " + qrData);
  //          Log.d("DataProcessingService", "Received textFromMain: " + textFromMain);
            Log.d("DataProcessingService", "Received textFromMain1: " + statusBarColor);
            Log.d("DataProcessingService", "Received textFromMain2: " + toolbarColor);

            // Gửi dữ liệu đến ứng dụng khác
        //    sendDataToAnotherApp(qrData, textFromMain);
        } else {
            Log.d("DataProcessingService", "Intent is null");
        }
        return START_STICKY;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("app2", "Service Unbound");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("app2", "Service Destroyed");
    }

    public String processReceivedData() {
        // Thực hiện xử lý dữ liệu
        return reciverData;
    }
     public int processReceivedDataColorStatus() {
        // Thực hiện xử lý dữ liệu
        return statusBarColor;
    }
     public int processReceivedDataColorToolbar() {
        // Thực hiện xử lý dữ liệu
        return toolbarColor;
    }


}