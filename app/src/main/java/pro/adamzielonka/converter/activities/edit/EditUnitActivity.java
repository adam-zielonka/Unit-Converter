package pro.adamzielonka.converter.activities.edit;

import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.models.file.Prefix;
import pro.adamzielonka.itemsview.classes.Item;
import pro.adamzielonka.itemsview.tools.Tests;

import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Language.getLanguageWords;
import static pro.adamzielonka.lib.Number.doubleToString;

public class EditUnitActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_unit);
        super.addItems();

        ArrayAdapter<Prefix> adapter = new MyArrayAdapter<Prefix>(getApplicationContext(), unit.prefixes) {
            @Override
            public void setView(Prefix item, TextView textPrimary, TextView textSecondary) {
                String description = getLanguageWords(item.description, measure.global);
                String prefixName = item.symbol + (!description.isEmpty() ? " - " + description : "");
                String exponent = doubleToString(unit.expBase) + "E" + doubleToString(item.exp);
                textPrimary.setText(prefixName);
                textSecondary.setText(exponent);
            }
        };

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_unit)
                .setTitle(R.string.list_item_symbol)
                .setUpdate(() -> unit.symbol)
                .setAction(symbol -> unit.symbol = unitName = (String) symbol)
                .addValidator(symbol -> Tests.isUnique(symbol, measure.units),
                        getString(R.string.error_symbol_unit_already_exist))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_description)
                .setUpdate(() -> measure.getWords(unit.descriptionPrefix, measure.global)
                        + measure.getWords(unit.description, measure.global))
                .setAction(() -> startEditActivity(EditDescriptionActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_formula)
                .setUpdate(() -> getFormula(unit.one, unit.shift, unit.shift2, unit.symbol))
                .setAction(() -> startEditActivity(EditFormulaActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_title_exponentiation_base)
                .setUpdate(() -> unit.expBase)
                .setAction(number -> unit.expBase = (Double) number)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_prefixes)
                .setAdapter(adapter)
                .setUpdate(() -> unit.prefixes)
                .setAction(position -> {
                    prefix = adapter.getItem((int) position);
                    startEditActivity(EditPrefixActivity.class);
                }).add(itemsView);

        new Item.Builder(this)
                .setTitle(R.string.list_item_add_prefix)
                .setAction(this::newPrefix)
                .addValidator(symbol -> Tests.isUnique(symbol, unit.prefixes),
                        getString(R.string.error_symbol_prefix_already_exist))
                .addValidator(symbol -> !symbol.equals(""), getString(R.string.error_symbol_empty))
                .add(itemsView);
    }

    private void newPrefix(Object symbol) {
        Prefix prefixTemp = prefix = new Prefix();
        prefix.symbol = (String) symbol;
        unit.prefixes.add(prefix);
        itemsView.onSave();
        prefix = prefixTemp;
        startEditActivity(EditPrefixActivity.class);
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
                            measure.units.remove(unit);
                            onSave();
                            onBackPressed();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
