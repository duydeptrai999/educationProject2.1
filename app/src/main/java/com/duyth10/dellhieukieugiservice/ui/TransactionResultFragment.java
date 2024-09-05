package com.duyth10.dellhieukieugiservice.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duyth10.dellhieukieugiservice.DataProcessingService;
import com.duyth10.dellhieukieugiservice.R;

public class TransactionResultFragment extends BaseFragment {

    private TextView qrDataTextView;
    private TextView textFromMainMonney;
    private ImageView checkV;

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

        return view;
    }

    private void stopDataProcessingService() {
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

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("TransactionResult", "Target Activity or Service not found.");
        }
    }
}
