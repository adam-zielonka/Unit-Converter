package pro.adamzielonka.converter.tools;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import pro.adamzielonka.converter.R;

public class ListItems {
    public static View getItemHeader(Activity activity, String text) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_list_title, null);
        ((TextView) view.findViewById(R.id.textHeader)).setText(text);
        return view;
    }

    public static View getItemNormal(Activity activity, String textPrimary, String textSecondary) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_list_item, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
        return view;
    }

    public static View getItemNormal(Activity activity, String textPrimary) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_list_item, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        return view;
    }

    public static View getItemDisable(Activity activity, String textPrimary, String textSecondary) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_list_item, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setTextColor(activity.getResources().getColor(R.color.colorGreyAccent));
        ((TextView) view.findViewById(R.id.textSecondary)).setTextColor(activity.getResources().getColor(R.color.colorGreyPrimary));
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
        return view;
    }
}
