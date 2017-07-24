package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;

import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class PrefixesAdapter extends ArrayAdapter<Prefix> {
    private final Unit unit;

    public PrefixesAdapter(@NonNull Context context, Unit unit) {
        super(context, R.layout.item_pref, unit.prefixes);
        this.unit = unit;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pref, parent, false)
                : convertView;

        Prefix item = getItem(position);
        if (item != null) {
            String prefixName = item.symbol + " - " + item.description;
            String exponent = doubleToString(unit.expBase) + "E" + doubleToString(item.exp);
            ((TextView) result.findViewById(R.id.textPrimary)).setText(prefixName);
            ((TextView) result.findViewById(R.id.textSecondary)).setText(exponent);
        }
        return result;
    }
}
