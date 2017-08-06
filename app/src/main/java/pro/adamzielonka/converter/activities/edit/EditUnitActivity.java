package pro.adamzielonka.converter.activities.edit;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.items.classes.Item;
import pro.adamzielonka.items.tools.Tests;

import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Language.getLanguageWords;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class EditUnitActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_unit);
        super.addItems();

        ArrayAdapter<Prefix> adapter = new MyArrayAdapter<Prefix>(getApplicationContext(), unit.prefixes) {
            @Override
            public void setView(Prefix item, TextView textPrimary, TextView textSecondary) {
                String description = getLanguageWords(item.description, userMeasure.global);
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
                .addValidator(symbol -> Tests.isUnique(symbol, userMeasure.units),
                        getString(R.string.error_symbol_unit_already_exist))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_description)
                .setUpdate(() -> userMeasure.getWords(unit.descriptionPrefix, userMeasure.global)
                        + userMeasure.getWords(unit.description, userMeasure.global))
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

//    @Override
//    protected void addActions() {
//        addActionDelete(R.string.delete_unit_title, () -> userMeasure.units.remove(unit));
//    }
}
