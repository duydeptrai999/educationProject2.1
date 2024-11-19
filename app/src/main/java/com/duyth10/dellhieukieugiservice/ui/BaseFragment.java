package com.duyth10.dellhieukieugiservice.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.IBinder;
import android.widget.TextView;

import com.duyth10.dellhieukieugiservice.service.DataProcessingService;
import com.duyth10.dellhieukieugiservice.viewmodel.MainViewModel;

public abstract class BaseFragment extends Fragment {

    protected MainViewModel viewModel;
    protected DataProcessingService mService;
    protected boolean mBound = false;
    protected TextView textFromMain;
//
//    protected ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            DataProcessingService.LocalBinder binder = (DataProcessingService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//            if (mService != null) {
//                String data = mService.processReceivedData();
//                updateUI(data);
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mBound = false;
//        }
//    };

//    @Override
//    public void onStart() {
//        super.onStart();
//        bindDataProcessingService();
//
//        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
//    }
//
//    protected void bindDataProcessingService() {
//        Intent intent = new Intent(getActivity(), DataProcessingService.class);
//        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mBound) {
//            getActivity().unbindService(connection);
//            mBound = false;
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (mBound) {
//            getActivity().unbindService(connection);
//            mBound = false;
//        }
//    }

    protected void updateUI(String data) {
        if (textFromMain != null) {
            textFromMain.setText(data);
        }
    }

    protected void navigateBack(String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
