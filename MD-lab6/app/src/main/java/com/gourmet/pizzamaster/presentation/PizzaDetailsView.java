package com.gourmet.pizzamaster.presentation;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.gourmet.pizzamaster.R;
import com.gourmet.pizzamaster.logic.VaultManager;
import com.gourmet.pizzamaster.model.PizzaItem;

public class PizzaDetailsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pizza_details);

        long refId = getIntent().getLongExtra("TARGET_ID", -1);
        PizzaItem item = VaultManager.getCore().getById(refId);

        ImageView topImage = findViewById(R.id.detailHeroImage);
        TextView headTitle = findViewById(R.id.detailLabel);
        TextView priceText = findViewById(R.id.detailPrice);
        TextView ingredientsText = findViewById(R.id.detailComponents);
        TextView descriptionText = findViewById(R.id.detailBio);
        TextView recipeSteps = findViewById(R.id.detailWorkflow);

        if (item != null) {
            topImage.setImageResource(item.getDrawableId());
            headTitle.setText(item.getLabel());
            priceText.setText(String.format("Special Price: %.2f $", item.getCost()));
            ingredientsText.setText(item.getComponents());
            descriptionText.setText(item.getBio());
            recipeSteps.setText(item.getWorkflow());
        }
    }
}
