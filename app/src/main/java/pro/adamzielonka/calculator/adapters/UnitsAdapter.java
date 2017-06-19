package pro.adamzielonka.calculator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import pro.adamzielonka.calculator.R;

public class UnitsAdapter extends BaseAdapter {
    private final String[] units;

    public UnitsAdapter(String[] units) {
        this.units = units;
    }

    @Override
    public int getCount() {
        return units.length;
    }

    @Override
    public String getItem(int position) {
        return units[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.units_layout, parent, false);
        } else {
            result = convertView;
        }

        String item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item);

        return result;
    }
}