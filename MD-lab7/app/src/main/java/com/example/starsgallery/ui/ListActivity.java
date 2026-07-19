package com.example.starsgallery.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ShareCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.starsgallery.R;
import com.example.starsgallery.adapter.CelebrityDisplayAdapter;
import com.example.starsgallery.service.CelebrityManager;

public class ListActivity extends AppCompatActivity {

    private CelebrityDisplayAdapter celebrityAdapter;
    private RecyclerView celebrityRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        celebrityRecyclerView = findViewById(R.id.recycle_view);
        celebrityAdapter = new CelebrityDisplayAdapter(this, CelebrityManager.getVault().getAll());
        
        celebrityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        celebrityRecyclerView.setAdapter(celebrityAdapter);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Celebrity Gallery");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchWidget = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchWidget.setQueryHint("Find a star...");
        searchWidget.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                if (celebrityAdapter != null) {
                    celebrityAdapter.getFilter().filter(input);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType("text/plain")
                    .setChooserTitle("Invite friends to Gallery")
                    .setText("Check out this amazing Celebrity Gallery app!")
                    .startChooser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
