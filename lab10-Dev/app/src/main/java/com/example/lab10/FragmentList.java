package com.example.lab10;

import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.widget.ArrayAdapter;

public class FragmentList extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] items = {
            "Item 1", "Item 2", "Item 3", "Item 4",
            "Item 5", "Item 6", "Item 7", "Item 8",
            "Item 9", "Item 10"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                items
        );
        setListAdapter(adapter);
    }
}