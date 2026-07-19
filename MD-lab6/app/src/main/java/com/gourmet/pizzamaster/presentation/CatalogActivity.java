package com.gourmet.pizzamaster.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.gourmet.pizzamaster.R;
import com.gourmet.pizzamaster.logic.VaultManager;
import com.gourmet.pizzamaster.model.PizzaItem;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView listView = findViewById(R.id.catalogListView);
        List<PizzaItem> data = VaultManager.getCore().getAllEntries();

        GourmetAdapter adapter = new GourmetAdapter(this, data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(CatalogActivity.this, PizzaDetailsView.class);
            intent.putExtra("TARGET_ID", id);
            startActivity(intent);
        });
    }
}
