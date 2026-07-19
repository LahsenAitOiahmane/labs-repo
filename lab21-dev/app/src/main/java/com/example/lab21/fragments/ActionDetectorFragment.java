package com.example.lab21.fragments;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab21.utils.MathFilter;
import com.google.android.material.card.MaterialCardView;

public class ActionDetectorFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView actionText;
    private MathFilter filter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.parseColor("#F5F5F6"));

        TextView titleView = new TextView(requireContext());
        titleView.setText("Détecteur d'Activité");
        titleView.setTextSize(24);
        titleView.setTextColor(Color.parseColor("#3F51B5"));
        titleView.setPadding(0, 0, 0, 24);

        MaterialCardView card = new MaterialCardView(requireContext());
        card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        card.setCardElevation(4f);
        card.setRadius(16f);
        card.setCardBackgroundColor(Color.WHITE);

        actionText = new TextView(requireContext());
        actionText.setTextSize(18);
        actionText.setPadding(48, 48, 48, 48);
        actionText.setTextColor(Color.parseColor("#424242"));
        card.addView(actionText);

        layout.addView(titleView);
        layout.addView(card);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        filter = new MathFilter(30, 0.8f);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        } else {
            actionText.setText("Accéléromètre non disponible.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        filter.applyLowPass(x, y, z);
        float movement = filter.getLinearMagnitude(x, y, z);
        filter.addValue(movement);

        String currentAction = estimateAction(x, y, z);

        actionText.setText(String.format("X : %.1f | Y : %.1f | Z : %.1f\n\nIntensité mvt : %.2f\n\nÉtat : %s", x, y, z, movement, currentAction));
    }

    private String estimateAction(float x, float y, float z) {
        if (!filter.isCalibrated()) return "Calibrage en cours...";

        float max = filter.getMax();
        float stdDev = filter.getStandardDeviation();

        if (max > 12f) return "Saut détécté !";
        if (stdDev > 1.5f) return "En train de marcher";
        
        if (Math.abs(z) > 8f) return "Téléphone posé à plat / Stable";
        if (Math.abs(y) > 7f || Math.abs(x) > 7f) return "Position verticale (Assis/Debout)";

        return "Au repos";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
