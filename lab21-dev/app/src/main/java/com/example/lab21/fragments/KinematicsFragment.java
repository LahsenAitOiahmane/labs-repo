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

import com.example.lab21.views.DynamicCurveView;
import com.google.android.material.card.MaterialCardView;

public class KinematicsFragment extends Fragment implements SensorEventListener {

    private static final String ARG_TYPE = "sensor_type";
    private static final String ARG_TITLE = "title";

    private SensorManager sensorManager;
    private Sensor motionSensor;

    private TextView detailsText;
    private DynamicCurveView curveView;

    public static KinematicsFragment newInstance(int type, String title) {
        KinematicsFragment fragment = new KinematicsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int sType = getArguments() != null ? getArguments().getInt(ARG_TYPE) : Sensor.TYPE_ACCELEROMETER;
        String sTitle = getArguments() != null ? getArguments().getString(ARG_TITLE) : "Mouvement";

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        motionSensor = sensorManager.getDefaultSensor(sType);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.parseColor("#F5F5F6"));

        TextView titleView = new TextView(requireContext());
        titleView.setText(sTitle);
        titleView.setTextSize(24);
        titleView.setTextColor(Color.parseColor("#3F51B5"));
        titleView.setPadding(0, 0, 0, 16);

        MaterialCardView valueCard = new MaterialCardView(requireContext());
        valueCard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        valueCard.setCardElevation(4f);
        valueCard.setRadius(16f);
        valueCard.setCardBackgroundColor(Color.WHITE);

        detailsText = new TextView(requireContext());
        detailsText.setTextSize(16);
        detailsText.setPadding(32, 32, 32, 32);
        detailsText.setTextColor(Color.parseColor("#212121"));
        valueCard.addView(detailsText);

        MaterialCardView chartCard = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);
        chartParams.setMargins(0, 32, 0, 0);
        chartCard.setLayoutParams(chartParams);
        chartCard.setCardElevation(4f);
        chartCard.setRadius(16f);
        chartCard.setCardBackgroundColor(Color.WHITE);

        curveView = new DynamicCurveView(requireContext());
        chartCard.addView(curveView);

        layout.addView(titleView);
        layout.addView(valueCard);
        layout.addView(chartCard);

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (motionSensor != null) {
            sensorManager.registerListener(this, motionSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            detailsText.setText("Capteur non supporté par cet appareil.");
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
        float mag = (float) Math.sqrt(x*x + y*y + z*z);

        detailsText.setText(String.format("Axe X : %.2f\nAxe Y : %.2f\nAxe Z : %.2f\nNorme : %.2f", x, y, z, mag));
        curveView.appendValue(mag);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
