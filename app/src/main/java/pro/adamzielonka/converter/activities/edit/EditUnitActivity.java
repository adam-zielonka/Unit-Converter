package pro.adamzielonka.converter.activities.edit;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.tools.Test;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.tools.Tests;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Language.getLanguageWords;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class EditUnitActivity extends EditActivity {

    @Override
    protected void addItems() {
        setTitle(R.string.title_activity_edit_unit);

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

        addItemTitle(R.string.list_title_unit);
        addItemText(R.string.list_item_symbol, () -> unit.symbol, symbol -> unit.symbol = unitName = symbol,
                new Test(symbol -> Tests.isUnique(symbol, userMeasure.units), R.string.error_symbol_unit_already_exist));
        addItemText(R.string.list_item_description, () -> userMeasure.getWords(unit.descriptionPrefix, userMeasure.global) + userMeasure.getWords(unit.description, userMeasure.global),
                () -> startActivityForResult(setEditIntent(EditDescriptionActivity.class), REQUEST_EDIT_ACTIVITY));
        addItemText(R.string.list_item_formula, () -> getFormula(unit.one, unit.shift, unit.shift2, unit.symbol),
                () -> startActivityForResult(setEditIntent(EditFormulaActivity.class), REQUEST_EDIT_ACTIVITY));
        addItemNumber(R.string.list_title_exponentiation_base, () -> unit.expBase, number -> unit.expBase = number);
        addItemTitle(R.string.list_title_prefixes);
        addItemsAdapter(adapter, () -> unit.prefixes, position -> {
            prefix = adapter.getItem(position);
            startActivityForResult(setEditIntent(EditPrefixActivity.class), REQUEST_EDIT_ACTIVITY);
        });
        addItem(R.string.list_item_add_prefix, () -> newAlertDialogCreate(R.string.dialog_prefix_symbol, EditPrefixActivity.class,
                this::newPrefix, new Test(symbol -> Tests.isUnique(symbol, unit.prefixes), R.string.error_symbol_prefix_already_exist)));
    }

    private void newPrefix(String symbol) {
        prefix = new Prefix();
        prefix.symbol = symbol;
        unit.prefixes.add(prefix);
    }

    @Override
    protected void addActions() {
        addActionDelete(R.string.delete_unit_title, () -> userMeasure.units.remove(unit));
    }
}
