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

public class OrderAdapter extends MyAdapter<CUnit> {


    public OrderAdapter(@NonNull Context context, List<CUnit> cUnits, String langCode, String globalCode) {
        super(langCode, globalCode, context, R.layout.item_order, cUnits);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false)
                : convertView;

        CUnit item = getItem(position);
        if (item != null) {
            String unitName = item.name + " - " + getLanguageWords(item.description);
            ((TextView) result.findViewById(R.id.textPrimary)).setText(unitName);
            ((TextView) result.findViewById(R.id.textPos)).setText(getChangePosition(item));
        }
        return result;
    }

    private String getChangePosition(CUnit item) {
        Integer result = item.basicPosition - item.position;
        if (result == 0) return "";

        result = (result + 1) / 2;

        if (result < 0) return result.toString();
        return "+" + result.toString();
    }
}