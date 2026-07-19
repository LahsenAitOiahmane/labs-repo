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

import com.google.android.material.card.MaterialCardView;

public class NavigationCompassFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accSensor;
    private Sensor magSensor;

    private TextView compassText;

    private final float[] gravityData = new float[3];
    private final float[] magneticData = new float[3];

    private boolean hasG = false;
    private boolean hasM = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.parseColor("#F5F5F6"));

        TextView titleView = new TextView(requireContext());
        titleView.setText("Boussole de Navigation");
        titleView.setTextSize(24);
        titleView.setTextColor(Color.parseColor("#3F51B5"));
        titleView.setPadding(0, 0, 0, 24);

        MaterialCardView card = new MaterialCardView(requireContext());
        card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        card.setCardElevation(4f);
        card.setRadius(16f);
        card.setCardBackgroundColor(Color.WHITE);

        compassText = new TextView(requireContext());
        compassText.setTextSize(20);
        compassText.setPadding(48, 48, 48, 48);
        compassText.setTextColor(Color.parseColor("#424242"));
        card.addView(compassText);

        layout.addView(titleView);
        layout.addView(card);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accSensor != null && magSensor != null) {
            sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            compassText.setText("Capteurs insuffisants pour la boussole.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravityData, 0, 3);
            hasG = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticData, 0, 3);
            hasM = true;
        }

        if (hasG && hasM) {
            float[] R = new float[9];
            float[] I = new float[9];
            if (SensorManager.getRotationMatrix(R, I, gravityData, magneticData)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);
                if (azimuth < 0) azimuth += 360;

                compassText.setText(String.format("Cap : %.1f°\nDirection : %s", azimuth, getCardinal(azimuth)));
            }
        }
    }

    private String getCardinal(float degree) {
        if (degree >= 337.5 || degree < 22.5) return "Nord";
        if (degree < 67.5) return "Nord-Est";
        if (degree < 112.5) return "Est";
        if (degree < 157.5) return "Sud-Est";
        if (degree < 202.5) return "Sud";
        if (degree < 247.5) return "Sud-Ouest";
        if (degree < 292.5) return "Ouest";
        return "Nord-Ouest";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
