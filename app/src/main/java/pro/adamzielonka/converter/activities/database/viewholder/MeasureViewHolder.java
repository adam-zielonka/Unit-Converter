package pro.adamzielonka.converter.activities.database.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.database.DataBaseMeasure;

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

    public void bindToPost(DataBaseMeasure dataBaseMeasure, View.OnClickListener starClickListener) {
        titleView.setText(dataBaseMeasure.title);
        versionView.setText(String.format("  v.%s", dataBaseMeasure.version));
        numStarsView.setText(String.valueOf(dataBaseMeasure.starCount));
        bodyView.setText(dataBaseMeasure.units_symbols);
        authorView.setText(dataBaseMeasure.author);
        if (starClickListener != null)
            starView.setOnClickListener(starClickListener);
    }
}
