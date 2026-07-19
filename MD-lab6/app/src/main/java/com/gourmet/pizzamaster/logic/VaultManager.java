package com.gourmet.pizzamaster.logic;

import com.gourmet.pizzamaster.R;
import com.gourmet.pizzamaster.model.PizzaItem;
import com.gourmet.pizzamaster.data.RepositoryInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VaultManager implements RepositoryInterface<PizzaItem> {

    private static VaultManager core;
    private final List<PizzaItem> storage = new ArrayList<>();

    private VaultManager() {
        populate();
    }

    public static VaultManager getCore() {
        if (core == null) core = new VaultManager();
        return core;
    }

    private void populate() {
        this.add(new PizzaItem("BBQ CHICKEN DELIGHT", 12.99, R.drawable.pizza_real_1, "35m", 
                "- 2 boneless skinless chicken breast halves\n- 1 cup barbecue sauce\n- 1 pizza crust\n- 2 cups shredded Gouda", 
                "A smoky and savory masterpiece with grilled chicken and rich BBQ sauce.", 
                "STEP 1: Grill chicken.\nSTEP 2: Spread sauce on crust.\nSTEP 3: Add chicken and onions.\nSTEP 4: Bake until cheese melts."));

        this.add(new PizzaItem("FRESH BRUSCHETTA", 14.50, R.drawable.pizza_real_2, "25m", 
                "- Plum tomatoes\n- Fresh basil\n- Garlic cloves\n- Balsamic vinegar", 
                "Garden fresh flavors on a crispy base, perfect for a light summer dinner.", 
                "STEP 1: Bake crust with cheese.\nSTEP 2: Mix tomatoes with herbs and oil.\nSTEP 3: Spoon over hot pizza."));

        this.add(new PizzaItem("GREEN SPINACH FEAST", 11.00, R.drawable.pizza_real_3, "20m", 
                "- Alfredo sauce\n- Chopped spinach\n- Italian cheese blend\n- Sliced tomatoes", 
                "Creamy and healthy vegetarian choice that even kids will love.", 
                "STEP 1: Spread Alfredo sauce.\nSTEP 2: Layer spinach and tomatoes.\nSTEP 3: Top with cheese and bake."));

        this.add(new PizzaItem("DEEP-DISH SAUSAGE", 18.00, R.drawable.pizza_real_4, "45m", 
                "- Italian sausage\n- Mozzarella cheese\n- Green peppers\n- Oregano and basil", 
                "A hearty, thick-crust pizza loaded with sausage and aromatic herbs.", 
                "STEP 1: Prepare deep-dish dough.\nSTEP 2: Layer cheese then sausage.\nSTEP 3: Add peppers and bake until golden."));

        this.add(new PizzaItem("HOMESTYLE BEEF", 13.00, R.drawable.pizza_real_5, "50m", 
                "- Ground beef\n- Tomato sauce\n- Green peppers\n- Mozzarella", 
                "Classic homemade pizza with a crisp, golden crust and zesty beef.", 
                "STEP 1: Knead and rise dough.\nSTEP 2: Cook beef and onions.\nSTEP 3: Assemble and bake at 400°."));

        this.add(new PizzaItem("PESTO CHICKEN BLAZE", 15.00, R.drawable.pizza_real_6, "50m", 
                "- Prepared pesto\n- Julienne peppers\n- Sliced mushrooms\n- Chicken breast", 
                "Incredible flavors where simple spices help the chicken and vegetables shine.", 
                "STEP 1: Prepare yeast dough.\nSTEP 2: Sauté chicken and veggies.\nSTEP 3: Spread pesto, add toppings and bake."));

        this.add(new PizzaItem("MEXICAN FUSION", 12.00, R.drawable.pizza_real_1, "30m", 
                "- Black beans\n- Jalapeno pepper\n- Pepper jack cheese\n- fresh cilantro", 
                "A healthy pizza with a kick of spice and plenty of protein.", 
                "STEP 1: Mash beans with spices.\nSTEP 2: Spread on crust with veggies.\nSTEP 3: Bake until cheese is bubbly."));

        this.add(new PizzaItem("BACON CHEESEBURGER", 14.00, R.drawable.pizza_real_2, "20m", 
                "- Ground beef\n- Crispy bacon\n- Dill pickles\n- Cheddar cheese", 
                "Combines two family favorites into one delicious, easy-to-make meal.", 
                "STEP 1: Brown beef and onions.\nSTEP 2: Top crust with beef, bacon, and pickles.\nSTEP 3: Bake with plenty of cheese."));

        this.add(new PizzaItem("ROYAL MARGHERITA", 10.00, R.drawable.pizza_real_3, "30m", 
                "- Roma tomatoes\n- Fresh mozzarella\n- Basil leaves\n- Olive oil", 
                "The legendary Italian flag pizza: red, white, and green.", 
                "STEP 1: Roll thin dough.\nSTEP 2: Layer tomatoes and cheese.\nSTEP 3: Bake and garnish with fresh basil."));

        this.add(new PizzaItem("STUFFED PEPPERONI", 16.00, R.drawable.pizza_real_4, "45m", 
                "- Pepperoni slices\n- Italian sausage\n- Double cheese\n- Pizza sauce", 
                "A decadent, stuffed-crust pizza that's a meal in itself.", 
                "STEP 1: Create double layer of dough.\nSTEP 2: Stuff with meats and cheese.\nSTEP 3: Seal edges and bake until brown."));
    }

    @Override
    public PizzaItem add(PizzaItem item) {
        storage.add(item);
        return item;
    }

    @Override
    public PizzaItem modify(PizzaItem item) {
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).getUniqueId() == item.getUniqueId()) {
                storage.set(i, item);
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean remove(long id) {
        return storage.removeIf(p -> p.getUniqueId() == id);
    }

    @Override
    public PizzaItem getById(long id) {
        for (PizzaItem p : storage) if (p.getUniqueId() == id) return p;
        return null;
    }

    @Override
    public List<PizzaItem> getAllEntries() {
        return Collections.unmodifiableList(storage);
    }
}
