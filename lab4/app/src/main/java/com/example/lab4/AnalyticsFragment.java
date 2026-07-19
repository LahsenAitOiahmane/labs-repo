package com.example.lab4;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.slider.Slider;

public class AnalyticsFragment extends Fragment {

    private TextView metricLabel;
    private Slider valueSlider;
    private float currentVal = 0.0f;
    private static final String STATE_VAL = "current_val";

    public AnalyticsFragment() {
        super(R.layout.layout_analytics);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        metricLabel = view.findViewById(R.id.progress_indicator);
        valueSlider = view.findViewById(R.id.data_slider);

        if (savedInstanceState != null) {
            currentVal = savedInstanceState.getFloat(STATE_VAL, 0.0f);
            valueSlider.setValue(currentVal);
            updateUI(currentVal);
        }

        valueSlider.addOnChangeListener((slider, value, fromUser) -> {
            currentVal = value;
            updateUI(value);
        });
    }

    private void updateUI(float val) {
        metricLabel.setText(String.format("Current Level: %.0f%%", val));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(STATE_VAL, currentVal);
    }
}