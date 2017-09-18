package pro.adamzielonka.items;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

public class HeaderItemBuilder {
    private String title;
    private Activity activity;

    public HeaderItemBuilder(Activity activity) {
        this.activity = activity;
    }

    public HeaderItemBuilder setTitle(@StringRes int title) {
        this.title = activity.getString(title);
        return this;
    }

    public HeaderItemBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public void add(ItemsView itemsView) {
        if (title != null) createItemHeader(itemsView);
    }

    private void createItemHeader(ItemsView itemsView) {
        View view = addItemHeader(title);
        itemsView.addItem(new Item(view));
    }

    private View addItemHeader(String text) {
        View view = activity.getLayoutInflater().inflate(R.layout.item_header, null);
        ((TextView) view.findViewById(R.id.textHeader)).setText(text);
        return view;
    }
}
