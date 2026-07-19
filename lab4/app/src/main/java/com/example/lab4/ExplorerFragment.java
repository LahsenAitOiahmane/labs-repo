package com.example.lab4;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class ExplorerFragment extends Fragment {

    private TextView statusDisplay;
    private MaterialButton triggerButton;

    public ExplorerFragment() {
        super(R.layout.layout_explorer);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusDisplay = view.findViewById(R.id.display_label);
        triggerButton = view.findViewById(R.id.action_trigger);

        triggerButton.setOnClickListener(v -> {
            statusDisplay.setText("System session initialized successfully.");
            statusDisplay.setTextColor(getResources().getColor(R.color.brand_primary));
        });
    }
}