package pro.adamzielonka.converter.activities.database.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.database.models.Measure;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView versionView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_title);
        versionView = itemView.findViewById(R.id.post_version);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
    }

    public void bindToPost(Measure measure, View.OnClickListener starClickListener) {
        titleView.setText(measure.title);
        versionView.setText(String.format("  v.%s", measure.version));
        authorView.setText(measure.author);
        numStarsView.setText(String.valueOf(measure.starCount));
        bodyView.setText(measure.units_symbols);

        starView.setOnClickListener(starClickListener);
    }
}
