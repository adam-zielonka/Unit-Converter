package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.units.cloud.CloudMeasure;

public class CloudMeasureAdapter extends ArrayAdapter<CloudMeasure> {

    public CloudMeasureAdapter(@NonNull Context context, @NonNull List<CloudMeasure> objects) {
        super(context, R.layout.layout_cloud_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View result = (convertView == null)
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cloud_item, parent, false)
                : convertView;

        CloudMeasure item = getItem(position);
        if (item != null) {
            ((TextView) result.findViewById(R.id.textPrimary)).setText(item.getName());
        }
        return result;
    }
}
