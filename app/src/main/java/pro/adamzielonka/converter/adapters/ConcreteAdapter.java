package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;

public class ConcreteAdapter extends MyAdapter<ConcreteUnit> {

    private int resource;

    public ConcreteAdapter(@NonNull Context context, int resource, @NonNull List<ConcreteUnit> objects, String langCode, String globalCode) {
        super(langCode, globalCode, context, resource, objects);
        this.resource = resource;
    }

    public ConcreteAdapter(@NonNull Context context, @NonNull List<ConcreteUnit> objects, String langCode, String globalCode) {
        super(langCode, globalCode, context, R.layout.spiner_units_small, objects);
        this.resource = R.layout.spiner_units_small;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.spiner_units, parent, false)
                : convertView;

        ConcreteUnit item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item != null ? item.name + " " : "");
        ((TextView) result.findViewById(R.id.unitsTextView2)).setText(item != null ? " " + getLanguageWords(item.description) : "");

        return result;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(resource, parent, false)
                : convertView;

        ConcreteUnit item = getItem(position);

        ((TextView) result.findViewById(R.id.unitsTextView)).setText(item != null ? item.name : "");
        TextView textView = result.findViewById(R.id.unitsTextView2);
        if (textView != null) textView.setText(item != null ? " " + item.description : "");

        return result;
    }
}