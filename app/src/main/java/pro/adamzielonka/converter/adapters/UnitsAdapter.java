package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;

public class UnitsAdapter extends ArrayAdapter<String[]> {

    public UnitsAdapter(@NonNull Context context, @NonNull String[][] objects) {
        super(context, R.layout.units_layout, objects);
    }

    public String getItemName(int position) {
        String[] item = getItem(position);
        return item != null ? item[0] : "";
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.units_layout, parent, false)
                : convertView;

        String[] item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item != null ? item[0] : "");
        ((TextView) result.findViewById(R.id.unitsTextView2)).setText(item != null ? item[1] : "");

        return result;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.units_small_layout, parent, false)
                : convertView;

        String[] item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item != null ? item[0] : "");

        return result;
    }
}