package pro.adamzielonka.calculator.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.calculator.R;

public class UnitsAdapter extends ArrayAdapter<String> {
    private final String[] units;

    public UnitsAdapter(@NonNull Context context, @NonNull String[] objects, @NonNull String[] units) {
        super(context, R.layout.units_layout, objects);
        this.units = units;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.units_layout, parent, false);
        } else {
            result = convertView;
        }

        String item = getItem(position);
        String unit = units[position];

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item);
        ((TextView) result.findViewById(R.id.unitsTextView2)).setText(unit);

        return result;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.units_small_layout, parent, false);
        } else {
            result = convertView;
        }

        String item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item);

        return result;
    }
}