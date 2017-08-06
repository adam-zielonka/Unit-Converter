package pro.adamzielonka.items.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.items.classes.Item;
import pro.adamzielonka.items.components.ItemsView;

public class ListViewActivity extends AppCompatActivity implements ItemsView.OnItemsUpdate {

    private Double number;
    private String text;
    private Boolean test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ItemsView itemsView = findViewById(R.id.ListView);
        itemsView.setOnItemsUpdate(this);
        itemsView.setEmptyAdapter();
        number = 123.456;
        text = "My Text";
        test = true;

        new Item.Builder(this)
                .setTitleHeader("Header")
                .add(itemsView);
        new Item.Builder(this)
                .setTitle("Number")
                .setUpdate(() -> number)
                .setAction(number -> this.number = (Double) number)
                .addValidator(number -> (Double) number > 0.0, "error")
                .add(itemsView);
        new Item.Builder(this)
                .setTitle("Text")
                .setUpdate(() -> text)
                .setAction(text -> this.text = (String) text)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle("Disabled Text")
                .setUpdate(() -> text)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader("Switcher")
                .add(itemsView);
        new Item.Builder(this)
                .setTitle("Switch me")
                .setUpdate(() -> test)
                .setAction(test -> this.test = (Boolean) test)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle("Switch state")
                .setUpdate(() -> test.toString())
                .add(itemsView);
        new Item.Builder(this)
                .setTitle("Text")
                .setIf(() -> test)
                .setUpdate(() -> text)
                .setAction(text -> this.text = (String) text)
                .add(itemsView);

        itemsView.onUpdate();
    }

    @Override
    public void onUpdate() {

    }
}
