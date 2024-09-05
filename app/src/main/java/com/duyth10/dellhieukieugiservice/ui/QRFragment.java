package com.duyth10.dellhieukieugiservice.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.duyth10.dellhieukieugiservice.DataProcessingService;
import com.duyth10.dellhieukieugiservice.R;
import com.duyth10.dellhieukieugiservice.viewmodel.MainViewModel;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class QRFragment extends Fragment {
    private MainViewModel viewModel;
    private PreviewView previewView;
    private TextView textFromMain;

    private boolean mBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataProcessingService.LocalBinder binder = (DataProcessingService.LocalBinder) service;
            DataProcessingService mService = binder.getService();
            mBound = true;
            if (mService != null) {
                String data = mService.processReceivedData();
                Log.d("app2text1", "ao ma : " + data);

                updateUI(data);
            }
            else {
                Log.d("ServiceConnection", "Service is null");
            }
            Log.d("app2text1", "ao ma : " + textFromMain.getText().toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_q_r, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        previewView = view.findViewById(R.id.previewView);
        textFromMain = view.findViewById(R.id.textFromMainApp);
        ImageView iconBack = view.findViewById(R.id.iconBack);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupCamera();
        observeViewModel();

        iconBack.setOnClickListener(v -> navigateBackToMainApp());
    }

    private void setupCamera() {
        viewModel.getCameraProviderFuture().addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = viewModel.getCameraProviderFuture().get();
                startCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("QRFragment", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void startCamera(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        viewModel.analyzeImage(imageAnalysis);

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void observeViewModel() {
        viewModel.getScanResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && !result.isEmpty()) {
                if (!TextUtils.isEmpty(textFromMain.getText().toString())) {
                    openResultFragment(result);
                }
                viewModel.clearScanResult();
            }
        });
    }

    private void navigateBackToMainApp() {
        if (!TextUtils.isEmpty(textFromMain.getText().toString())) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.duyth10.dellhieukieugi", "com.duyth10.dellhieukieugi.MainActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void openResultFragment(String qrData) {
        Bundle bundle = new Bundle();
        bundle.putString("qrData", qrData);
        bundle.putString("textFromMain", textFromMain.getText().toString());
        TransactionResultFragment resultFragment = new TransactionResultFragment();
        resultFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, resultFragment);
        transaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), DataProcessingService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        Log.d("app2text", "ao ma : " + textFromMain.getText().toString());

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

    private void updateUI(String data) {
        if (textFromMain != null) {
            textFromMain.setText(data);
        }
    }
}
