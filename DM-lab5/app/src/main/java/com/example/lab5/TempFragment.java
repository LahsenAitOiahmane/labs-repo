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

public class TempFragment extends Fragment {

    private RadioGroup unitSelector;
    private RadioButton btnCtoF, btnFtoC;
    private TextInputEditText tempInputField;
    private Button computeBtn;
    private TextView resultDisplay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_temp, container, false);

        unitSelector = rootView.findViewById(R.id.group_unit_selector);
        btnCtoF = rootView.findViewById(R.id.choice_c_to_f);
        btnFtoC = rootView.findViewById(R.id.choice_f_to_c);
        tempInputField = rootView.findViewById(R.id.input_temp_field);
        computeBtn = rootView.findViewById(R.id.action_calc_temp);
        resultDisplay = rootView.findViewById(R.id.display_temp_result);

        computeBtn.setOnClickListener(v -> {
            String rawValue = tempInputField.getText().toString();
            if (TextUtils.isEmpty(rawValue)) {
                Toast.makeText(getContext(), "Champ vide !", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double numericVal = Double.parseDouble(rawValue);
                double convertedVal;

                if (btnCtoF.isChecked()) {
                    convertedVal = (numericVal * 1.8) + 32;
                    resultDisplay.setText(String.format("%.2f °F", convertedVal));
                } else {
                    convertedVal = (numericVal - 32) / 1.8;
                    resultDisplay.setText(String.format("%.2f °C", convertedVal));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Format invalide", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}