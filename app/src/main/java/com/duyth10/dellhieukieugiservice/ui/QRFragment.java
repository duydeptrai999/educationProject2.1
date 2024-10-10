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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.duyth10.dellhieukieugiservice.DataProcessingService;
import com.duyth10.dellhieukieugiservice.R;
import com.duyth10.dellhieukieugiservice.databinding.FragmentQRBinding;
import com.duyth10.dellhieukieugiservice.viewmodel.MainViewModel;
import java.util.concurrent.ExecutionException;

public class QRFragment extends Fragment {
    public MainViewModel viewModel;
    public FragmentQRBinding binding;

    private boolean mBound = false;
    private DataProcessingService mService;

    public int statusbarColor;
    public int toolbarColor;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataProcessingService.LocalBinder binder = (DataProcessingService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if (mService != null) {
                String data = mService.processReceivedData();
                statusbarColor = mService.processReceivedDataColorStatus();
                toolbarColor = mService.processReceivedDataColorToolbar();

                Log.d("app2text1", "Data: " + data);
                Log.d("app2text1", "Status bar color: " + statusbarColor);
                Log.d("app2text1", "Toolbar color: " + toolbarColor);

                updateColors();
            } else {
                Log.d("ServiceConnection", "Service is null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_q_r, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupCamera();
        observeViewModel();

        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(statusbarColor);
        }

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(toolbarColor);
        }

        binding.iconBack.setOnClickListener(v -> navigateBackToMainApp());
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
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        viewModel.analyzeImage(imageAnalysis);

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void observeViewModel() {
        viewModel.getScanResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null && !result.isEmpty()) {
                if (!TextUtils.isEmpty(binding.textFromMainApp.getText().toString())) {
                    openResultFragment(result);
                }
                viewModel.clearScanResult();
            }
        });
    }

    private void navigateBackToMainApp() {
        if (!TextUtils.isEmpty(binding.textFromMainApp.getText().toString())) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.duyth10.dellhieukieugi", "com.duyth10.dellhieukieugi.MainActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void openResultFragment(String qrData) {
        Bundle bundle = new Bundle();
        bundle.putString("qrData", qrData);
        bundle.putString("textFromMain", binding.textFromMainApp.getText().toString());
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

        Log.d("app2text", "textFromMainApp: " + binding.textFromMainApp.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBound && mService != null) {
            updateColors();
        }
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
        binding = null;
    }

    public void updateUI(String data) {
        binding.textFromMainApp.setText(data);
    }

    public void updateColors() {
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(statusbarColor);
        }

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(toolbarColor);
        }
    }
}
