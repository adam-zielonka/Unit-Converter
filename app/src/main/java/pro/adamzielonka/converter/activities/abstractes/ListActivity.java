package pro.adamzielonka.converter.activities.abstractes;

import android.os.Bundle;
import android.widget.Toolbar;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.interfaces.AddItemsInterface;
import pro.adamzielonka.items.ItemsView;

public abstract class ListActivity extends BaseActivity implements AddItemsInterface {
    public ItemsView itemsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        itemsView = findViewById(R.id.ListView);

        addItems();

        itemsView.onUpdate();
    }
}

