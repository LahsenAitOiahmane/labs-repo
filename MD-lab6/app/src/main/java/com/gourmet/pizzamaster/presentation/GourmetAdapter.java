package com.gourmet.pizzamaster.presentation;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.gourmet.pizzamaster.R;
import com.gourmet.pizzamaster.model.PizzaItem;
import java.util.List;

public class GourmetAdapter extends BaseAdapter {
    private final Context context;
    private final List<PizzaItem> collection;

    public GourmetAdapter(Context context, List<PizzaItem> collection) {
        this.context = context;
        this.collection = collection;
    }

    @Override public int getCount() { return collection.size(); }
    @Override public Object getItem(int i) { return collection.get(i); }
    @Override public long getItemId(int i) { return collection.get(i).getUniqueId(); }

    @Override
    public View getView(int pos, View convert, ViewGroup parent) {
        if (convert == null)
            convert = LayoutInflater.from(context).inflate(R.layout.item_gourmet_pizza, parent, false);

        ImageView icon = convert.findViewById(R.id.pizzaThumbnail);
        TextView label = convert.findViewById(R.id.pizzaLabel);
        TextView info = convert.findViewById(R.id.pizzaSubtext);
        TextView cost = convert.findViewById(R.id.pizzaPriceTag);

        PizzaItem item = collection.get(pos);
        icon.setImageResource(item.getDrawableId());
        label.setText(item.getLabel());
        info.setText("Cooking: " + item.getPrepTime());
        cost.setText(String.format("%.2f $", item.getCost()));

        return convert;
    }
}
