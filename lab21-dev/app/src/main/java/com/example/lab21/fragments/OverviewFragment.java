package com.example.lab21.fragments;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab21.utils.SensorDetailHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class OverviewFragment extends Fragment {

    private SensorManager manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setBackgroundColor(Color.parseColor("#F5F5F6"));

        LinearLayout mainLayout = new LinearLayout(requireContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);

        scrollView.addView(mainLayout);

        manager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (manager != null) {
            List<Sensor> availableSensors = manager.getSensorList(Sensor.TYPE_ALL);

            TextView header = new TextView(requireContext());
            header.setText(availableSensors.size() + " capteurs détectés");
            header.setTextSize(20);
            header.setPadding(0, 0, 0, 32);
            header.setTextColor(Color.parseColor("#3F51B5"));
            mainLayout.addView(header);

            for (Sensor s : availableSensors) {
                MaterialCardView card = new MaterialCardView(requireContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 24);
                card.setLayoutParams(params);
                card.setCardElevation(4f);
                card.setRadius(16f);
                card.setCardBackgroundColor(Color.WHITE);

                TextView text = new TextView(requireContext());
                text.setText(SensorDetailHelper.getDetailedInfo(s));
                text.setPadding(32, 32, 32, 32);
                text.setTextSize(14);
                text.setTextColor(Color.parseColor("#424242"));

                card.addView(text);
                mainLayout.addView(card);
            }
        }

        return scrollView;
    }
}
