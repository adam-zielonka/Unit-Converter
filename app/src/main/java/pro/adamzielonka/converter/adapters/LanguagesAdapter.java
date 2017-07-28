package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pro.adamzielonka.converter.R;

public class LanguagesAdapter extends MyAdapter<String[]> {

    public LanguagesAdapter(@NonNull Context context, ArrayList<String[]> list) {
        super(context, R.layout.item_pref, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pref, parent, false)
                : convertView;

        String[] item = getItem(position);
        if (item != null) {
            ((TextView) result.findViewById(R.id.textPrimary)).setText(item[0]);
            ((TextView) result.findViewById(R.id.textSecondary)).setText(item[1]);
        }
        return result;
    }
}
