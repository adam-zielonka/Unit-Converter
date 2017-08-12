package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.concrete.CUnit;

public class ConverterAdapter extends MyAdapter<CUnit> {

    public ConverterAdapter(@NonNull Context context, @NonNull List<CUnit> objects, String langCode, String globalCode) {
        super(langCode, globalCode, context, R.layout.spiner_units_small, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.spiner_units, parent, false)
                : convertView;

        CUnit item = getItem(position);

        ((TextView) result.findViewById(R.id.textView)).setText(
                item != null ? item.name : "");
        ((TextView) result.findViewById(R.id.textView2)).setText(
                item != null ? getLanguageWords(item.description) : "");
        return result;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.spiner_units_small, parent, false)
                : convertView;

        CUnit item = getItem(position);
        ((TextView) result.findViewById(R.id.textView)).setText(item != null ? item.name : "");
        return result;
    }
}