package com.example.lab21.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private TextView stepsText;

    private float startSteps = -1;

    private final ActivityResultLauncher<String> permissionReq =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    initSensor();
                } else {
                    stepsText.setText("Permission refusée. Impossible de compter les pas.");
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.parseColor("#F5F5F6"));

        TextView titleView = new TextView(requireContext());
        titleView.setText("Podomètre");
        titleView.setTextSize(24);
        titleView.setTextColor(Color.parseColor("#3F51B5"));
        titleView.setPadding(0, 0, 0, 24);

        MaterialCardView card = new MaterialCardView(requireContext());
        card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        card.setCardElevation(4f);
        card.setRadius(16f);
        card.setCardBackgroundColor(Color.WHITE);

        stepsText = new TextView(requireContext());
        stepsText.setTextSize(18);
        stepsText.setPadding(48, 48, 48, 48);
        stepsText.setTextColor(Color.parseColor("#424242"));
        card.addView(stepsText);

        layout.addView(titleView);
        layout.addView(card);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stepSensor == null) {
            stepsText.setText("Capteur de pas non détecté sur ce dispositif.");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionReq.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            initSensor();
        }
    }

    private void initSensor() {
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float bootSteps = event.values[0];
        if (startSteps < 0) {
            startSteps = bootSteps;
        }
        int sessionCount = (int) (bootSteps - startSteps);

        stepsText.setText(String.format("Depuis redémarrage : %d pas\n\nSession actuelle : %d pas", (int) bootSteps, sessionCount));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
