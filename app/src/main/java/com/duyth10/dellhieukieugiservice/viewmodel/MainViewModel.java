package com.duyth10.dellhieukieugiservice.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private final MutableLiveData<String> scanResult = new MutableLiveData<>();
    private final MutableLiveData<String> textFrommain = new MutableLiveData<>();

    private final ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final BarcodeScanner scanner;

    public MainViewModel(@NonNull Application application) {
        super(application);
        scanner = BarcodeScanning.getClient();
        cameraProviderFuture = ProcessCameraProvider.getInstance(application);
    }

    public LiveData<String> getScanResult() {
        return scanResult;
    }

    public void setScanResult(String result) {
        scanResult.setValue(result); // Use setValue() on the main thread
    }

    public void postScanResult(String result) {
        scanResult.postValue(result);
    }
    public LiveData<String> getTextFromMain() {
        return textFrommain;
    }



    public ListenableFuture<ProcessCameraProvider> getCameraProviderFuture() {
        return cameraProviderFuture;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    public void analyzeImage(ImageAnalysis imageAnalysis) {
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(getApplication()), image -> {
            InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

            scanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            scanResult.postValue(rawValue);
                            Log.d("BarcodeScanner", "QR Code Value: " + rawValue);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("BarcodeScanner", "Error ", e))
                    .addOnCompleteListener(command -> image.close());
        });
    }

    public void clearScanResult() {
        scanResult.setValue(null);
    }

    public void setTextFrommain(String result) {
        textFrommain.postValue(result);
    }

}
