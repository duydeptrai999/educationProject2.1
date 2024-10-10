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
import androidx.appcompat.widget.Toolbar;

import com.duyth10.dellhieukieugiservice.DataProcessingService;
import com.duyth10.dellhieukieugiservice.R;

public class TransactionResultFragment extends BaseFragment {

    private TextView qrDataTextView;
    private TextView textFromMainMonney;
    private ImageView checkV;

    public int statusbarColor;
    public int toolbarcolor;

    public final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataProcessingService.LocalBinder binder = (DataProcessingService.LocalBinder) service;
            DataProcessingService mService = binder.getService();
            mBound = true;
            if (mService != null) {
                String data = mService.processReceivedData();
                statusbarColor = mService.processReceivedDataColorStatus();
                toolbarcolor = mService.processReceivedDataColorToolbar();
                Log.d("app2text1", "Received data: " + data);
                Log.d("app2text1", "Status bar color: " + statusbarColor);
                Log.d("app2text1", "Toolbar color: " + toolbarcolor);

                updateUI(data);
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

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_transaction_result, container, false);
        qrDataTextView = view.findViewById(R.id.qrDataTextView);
        checkV = view.findViewById(R.id.imageView);
        textFromMainMonney = view.findViewById(R.id.textMonney);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String qrData = args.getString("qrData");
            String textFromMain = args.getString("textFromMain");
            qrDataTextView.setText(qrData);
            textFromMainMonney.setText(textFromMain);
        }

        checkV.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(textFromMainMonney.getText().toString()) &&
                    !TextUtils.isEmpty(qrDataTextView.getText().toString())) {

                processAndSendData(qrDataTextView.getText().toString(), textFromMainMonney.getText().toString());
                stopDataProcessingService();
            }
        });

        updateColors();
    }

    public void updateColors() {
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(statusbarColor);
        }

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar1);
        if (toolbar != null) {
            toolbar.setBackgroundColor(toolbarcolor);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public void stopDataProcessingService() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), DataProcessingService.class);
            getActivity().stopService(intent);  // Dừng dịch vụ
        }
    }

    private void processAndSendData(String qrData, String textFromMain) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.duyth10.dellhieukieugi", "com.duyth10.dellhieukieugi.MainActivity"));
        intent.putExtra("qrData", qrData);
        intent.putExtra("textFromMain", textFromMain);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            Log.d("TransactionResult", "Target Activity or Service not found.");
        }
    }
}
