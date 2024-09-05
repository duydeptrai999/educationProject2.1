package com.duyth10.dellhieukieugiservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class FakeDataProcessingService extends Service {

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public FakeDataProcessingService getService() {
            return FakeDataProcessingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public String processReceivedData() {
        return "0000";
    }

    public int processReceivedDataColorStatus() {
        return 0xFF0000;
    }

    public int processReceivedDataColorToolbar() {
        return 0x00FF00;
    }
}
