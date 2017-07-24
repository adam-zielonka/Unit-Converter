package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;

public class OrderAdapter extends ArrayAdapter<ConcreteUnit> {


    public OrderAdapter(@NonNull Context context, List<ConcreteUnit> concreteUnits) {
        super(context, R.layout.item_order, concreteUnits);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false)
                : convertView;

        ConcreteUnit item = getItem(position);
        if (item != null) {
            String unitName = item.name + " - " + item.description;
            ((TextView) result.findViewById(R.id.textPrimary)).setText(unitName);
            ((TextView) result.findViewById(R.id.textPos)).setText(getChangePosition(item));
        }
        return result;
    }

    private String getChangePosition(ConcreteUnit item) {
        Integer result = item.basicPosition - item.position;
        if (result == 0) return "";

        result = (result + 1) / 2;

        if (result < 0) return result.toString();
        return "+" + result.toString();
    }
}