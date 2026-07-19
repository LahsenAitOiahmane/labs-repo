package com.example.lab10;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

public class TaskFragment extends ListFragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] taskArray = {
            "Update System Core", "Review Security Logs", "Backup Database",
            "Optimize Assets", "Client Meeting", "Refactor UI Module",
            "Sprint Planning", "Bug Scrub", "API Documentation"
        };

        ArrayAdapter<String> taskAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                taskArray
        );
        
        setListAdapter(taskAdapter);
        
        // Add a bit of padding to the list
        getListView().setPadding(16, 16, 16, 16);
        getListView().setClipToPadding(false);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
    }
}