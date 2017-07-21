package pro.adamzielonka.converter.activities.database.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.database.CloudMeasure;

public class MeasureViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView versionView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;

    public MeasureViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_title);
        versionView = itemView.findViewById(R.id.post_version);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
    }

    public void bindToPost(CloudMeasure cloudMeasure, View.OnClickListener starClickListener) {
        titleView.setText(cloudMeasure.title);
        versionView.setText(String.format("  v.%s", cloudMeasure.version));
        authorView.setText(cloudMeasure.author);
        numStarsView.setText(String.valueOf(cloudMeasure.starCount));
        bodyView.setText(cloudMeasure.units_symbols);

        starView.setOnClickListener(starClickListener);
    }
}
