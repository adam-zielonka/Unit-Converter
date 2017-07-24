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
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;

public class UnitsAdapter extends ArrayAdapter<Unit> {

    public UnitsAdapter(@NonNull Context context, @NonNull List<Unit> objects) {
        super(context, R.layout.item_pref, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pref, parent, false)
                : convertView;

        Unit item = getItem(position);
        if (item != null) {
            String unitName = item.symbol + " - " + item.descriptionPrefix + item.description;

            StringBuilder prefixes = new StringBuilder("");
            for (Prefix prefix : item.prefixes) {
                prefixes.append(prefix.symbol).append(item.symbol);
                prefixes.append(" ");
            }

            ((TextView) result.findViewById(R.id.textPrimary)).setText(unitName);
            TextView textPrefixes = result.findViewById(R.id.textSecondary);
            textPrefixes.setText(prefixes.toString());
            if (prefixes.toString().equals("")) textPrefixes.setVisibility(View.GONE);
        }
        return result;
    }
}
