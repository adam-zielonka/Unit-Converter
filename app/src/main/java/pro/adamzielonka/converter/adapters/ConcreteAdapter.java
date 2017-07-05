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
import pro.adamzielonka.converter.units.concrete.ConcreteUnit;

public class ConcreteAdapter extends ArrayAdapter<ConcreteUnit> {

    public ConcreteAdapter(@NonNull Context context, @NonNull List<ConcreteUnit> objects) {
        super(context, R.layout.units_layout, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.units_layout, parent, false)
                : convertView;

        ConcreteUnit item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item != null ? item.getName() + " " : "");
        ((TextView) result.findViewById(R.id.unitsTextView2)).setText(item != null ? " " + item.getDescription() : "");

        return result;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.units_small_layout, parent, false)
                : convertView;

        ConcreteUnit item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item != null ? item.getName() : "");

        return result;
    }
}