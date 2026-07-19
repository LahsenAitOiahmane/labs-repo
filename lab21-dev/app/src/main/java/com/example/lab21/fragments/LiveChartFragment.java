package com.example.lab21.fragments;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class LiveChartFragment extends Fragment implements SensorEventListener {

    private static final String ARG_TYPE = "type";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MODE = "mode";

    private SensorManager sensorManager;
    private Sensor targetSensor;

    private TextView valueDisplay;
    private DynamicCurveView curveView;

    private int sType;
    private String sTitle;
    private String sMode;

    private final Handler simHandler = new Handler(Looper.getMainLooper());
    private float simTick = 0f;

    public static LiveChartFragment newInstance(int type, String title, String mode) {
        LiveChartFragment fragment = new LiveChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            sType = getArguments().getInt(ARG_TYPE);
            sTitle = getArguments().getString(ARG_TITLE);
            sMode = getArguments().getString(ARG_MODE);
        }

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        targetSensor = sensorManager.getDefaultSensor(sType);

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
        
        valueDisplay = new TextView(requireContext());
        valueDisplay.setTextSize(18);
        valueDisplay.setPadding(32, 32, 32, 32);
        valueDisplay.setTextColor(Color.parseColor("#212121"));
        valueCard.addView(valueDisplay);

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
        if (targetSensor != null) {
            sensorManager.registerListener(this, targetSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            valueDisplay.setText("Capteur non détecté. Mode simulation activé.");
            runSimulation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        simHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float val = getProcessedValue(event.values);
        updateInterface(val);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private float getProcessedValue(float[] values) {
        if ("MAGNITUDE".equals(sMode)) {
            return (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        }
        return values[0];
    }

    private void updateInterface(float val) {
        valueDisplay.setText(String.format("Mesure instantanée : %.2f", val));
        curveView.appendValue(val);
    }

    private void runSimulation() {
        simHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                simTick += 1f;
                float val;
                if (sType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    val = 22f + (float) Math.sin(simTick / 4f) * 4f;
                } else if (sType == Sensor.TYPE_RELATIVE_HUMIDITY) {
                    val = 40f + (float) Math.sin(simTick / 6f) * 10f;
                } else if (sType == Sensor.TYPE_PROXIMITY) {
                    val = (simTick % 10 < 5) ? 0f : 8f;
                } else if (sType == Sensor.TYPE_MAGNETIC_FIELD) {
                    val = 50f + (float) Math.sin(simTick / 3f) * 15f;
                } else {
                    val = (float) Math.sin(simTick);
                }
                updateInterface(val);
                simHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}
