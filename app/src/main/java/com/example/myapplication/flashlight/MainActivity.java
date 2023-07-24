package com.example.myapplication.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends Activity {

    Switch switch1;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = findViewById(R.id.switch1);

        // Check if the device has a camera with flashlight
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            // Display an error message or handle the absence of flashlight on the device
            switch1.setEnabled(false);
            return;
        }

        // Get the camera manager
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Get the first camera's ID (assuming it has a flashlight)
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Set a listener for the Switch button
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Turn on the flashlight when the Switch button is switched on
                    turnOnFlashlight();
                } else {
                    // Turn off the flashlight when the Switch button is switched off
                    turnOffFlashlight();
                }
            }
        });
    }

    // Method to turn on the flashlight
    private void turnOnFlashlight() {
        if (!isFlashlightOn) {
            try {
                // Set the torch mode to true to turn on the flashlight
                cameraManager.setTorchMode(cameraId, true);
                isFlashlightOn = true;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to turn off the flashlight
    private void turnOffFlashlight() {
        if (isFlashlightOn) {
            try {
                // Set the torch mode to false to turn off the flashlight
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Turn off the flashlight when the app is paused
        turnOffFlashlight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Turn off the flashlight and release the camera resources when the app is closed
        releaseCamera();
    }

    // Method to release the camera resources
    private void releaseCamera() {
        if (cameraManager != null) {
            try {
                // Turn off the flashlight before releasing the camera
                if (isFlashlightOn) {
                    cameraManager.setTorchMode(cameraId, false);
                    isFlashlightOn = false;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            cameraManager = null;
            cameraId = null;
        }
    }
}


