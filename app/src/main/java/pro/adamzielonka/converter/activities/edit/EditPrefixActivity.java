package pro.adamzielonka.converter.activities.edit;

import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.items.classes.Item;
import pro.adamzielonka.items.tools.Tests;

public class EditPrefixActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_prefix);
        super.addItems();

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_prefix)
                .setTitle(R.string.list_item_symbol)
                .setUpdate(() -> prefix.symbol)
                .setAction(symbol -> prefix.symbol = prefixName = (String) symbol)
                .addValidator(symbol -> Tests.isUnique(symbol, unit.prefixes), getString(R.string.error_symbol_prefix_already_exist))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_description)
                .setUpdate(() -> userMeasure.getWords(prefix.description, userMeasure.global))
                .setAction(text -> prefix.description.put(userMeasure.global, (String) text))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_exponent)
                .setUpdate(() -> prefix.exp)
                .setAction(exp -> prefix.exp = (Double) exp)
                .add(itemsView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_unit_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_delete, (d, i) -> {
                            unit.prefixes.remove(prefix);
                            onSave();
                            onBackPressed();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
