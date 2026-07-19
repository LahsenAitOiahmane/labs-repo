package com.example.lab5;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;

public class DistanceFragment extends Fragment {

    private RadioGroup distTypeGroup;
    private RadioButton btnKmToMi, btnMiToKm;
    private TextInputEditText distanceInput;
    private Button runConversion;
    private TextView finalResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_distance, container, false);

        distTypeGroup = layoutView.findViewById(R.id.group_dist_selector);
        btnKmToMi = layoutView.findViewById(R.id.choice_km_to_mi);
        btnMiToKm = layoutView.findViewById(R.id.choice_mi_to_km);
        distanceInput = layoutView.findViewById(R.id.input_dist_field);
        runConversion = layoutView.findViewById(R.id.action_calc_dist);
        finalResult = layoutView.findViewById(R.id.display_dist_result);

        runConversion.setOnClickListener(v -> {
            String entry = distanceInput.getText().toString();
            if (TextUtils.isEmpty(entry)) {
                Toast.makeText(getContext(), "Veuillez saisir une distance", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(entry);
                double calculated;

                if (btnKmToMi.isChecked()) {
                    calculated = amount * 0.621371;
                    finalResult.setText(String.format("%.3f Miles", calculated));
                } else {
                    calculated = amount / 0.621371;
                    finalResult.setText(String.format("%.3f Km", calculated));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Entrée non valide", Toast.LENGTH_SHORT).show();
            }
        });

        return layoutView;
    }
}