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

public abstract class MyArrayAdapter<T> extends ArrayAdapter<T> implements IArrayAdapter<T> {
    protected MyArrayAdapter(@NonNull Context context, @NonNull List<T> objects) {
        super(context, R.layout.item_pref, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pref, parent, false)
                : convertView;

        T item = getItem(position);
        if (item != null) {
            setView(item, result.findViewById(R.id.textPrimary), result.findViewById(R.id.textSecondary));
        }
        return result;
    }
}

interface IArrayAdapter<T> {
    void setView(T item, TextView textPrimary, TextView textSecondary);
}
