package com.duyth10.dellhieukieugiservice.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duyth10.dellhieukieugiservice.DataProcessingService;
import com.duyth10.dellhieukieugiservice.viewmodel.MainViewModel;

import java.util.concurrent.ExecutionException;

public abstract class BaseFragment extends Fragment {

    protected MainViewModel viewModel;
    protected DataProcessingService mService;
    protected boolean mBound = false;
    protected TextView textFromMain;

    protected ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataProcessingService.LocalBinder binder = (DataProcessingService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if (mService != null) {
                String data = mService.processReceivedData();
                updateUI(data);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

<<<<<<< HEAD

=======
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b
    @Override
    public void onStart() {
        super.onStart();
        bindDataProcessingService();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    protected void bindDataProcessingService() {
        Intent intent = new Intent(getActivity(), DataProcessingService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(connection);
            mBound = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBound) {
            getActivity().unbindService(connection);
            mBound = false;
        }
    }

<<<<<<< HEAD
    public void updateUI(String data) {
=======
    protected void updateUI(String data) {
>>>>>>> 624ea7e5a9fedc335bdbd6a0c2528e9501b01f9b
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
