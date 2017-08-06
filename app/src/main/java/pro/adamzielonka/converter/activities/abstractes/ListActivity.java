package pro.adamzielonka.converter.activities.abstractes;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.items.components.ItemsView;

public abstract class ListActivity extends BaseActivity implements AddItemsInterface, ItemsView.OnItemsUpdate {
    public ItemsView itemsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemsView = findViewById(R.id.ListView);
        itemsView.setOnItemsUpdate(this);
        itemsView.setEmptyAdapter();

        addItems();

        itemsView.onUpdate();
    }
}

interface AddItemsInterface {
    void addItems();
}

