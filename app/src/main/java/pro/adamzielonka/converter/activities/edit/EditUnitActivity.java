package pro.adamzielonka.converter.activities.edit;

import android.view.View;
import android.widget.AdapterView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.PrefixesAdapter;
import pro.adamzielonka.converter.models.user.Prefix;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Converter.getFormula;

public class EditUnitActivity extends EditActivity {

    private View addPrefix;

    private PrefixesAdapter prefixesAdapter;

    @Override
    protected void addItems() {
        setTitle(R.string.title_activity_edit_unit);
        prefixesAdapter = new PrefixesAdapter(getApplicationContext(), unit, userMeasure.global, userMeasure.global);
        listView.setAdapter(prefixesAdapter);

        addItemTitle(R.string.list_title_unit);
        addItemText(R.string.list_item_symbol, () -> unit.symbol, symbol -> unit.symbol = unitName = symbol);
        addItemText(R.string.list_item_description, () -> userMeasure.getWords(unit.descriptionPrefix, userMeasure.global) + userMeasure.getWords(unit.description, userMeasure.global),
                () -> startActivityForResult(setEditIntent(EditDescriptionActivity.class), REQUEST_EDIT_ACTIVITY));
        addItemText(R.string.list_item_formula, () -> getFormula(unit.one, unit.shift, unit.shift2, unit.symbol),
                () -> startActivityForResult(setEditIntent(EditFormulaActivity.class), REQUEST_EDIT_ACTIVITY));
        addItemNumber(R.string.list_title_exponentiation_base, () -> unit.expBase, number -> unit.expBase = number);
        addItemTitle(R.string.list_title_prefixes);
        addPrefix = listView.addFooterItem(getString(R.string.list_item_add_prefix));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        prefixesAdapter.clear();
        prefixesAdapter.addAll(unit.prefixes);
        prefixesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (isAdapterItemClick(position)) {
            prefix = prefixesAdapter.getItem(getAdapterPosition(position));
            startActivityForResult(setEditIntent(EditPrefixActivity.class), REQUEST_EDIT_ACTIVITY);
        } else if (view.equals(addPrefix)) {
            newAlertDialogTextCreate(R.string.dialog_prefix_symbol, EditPrefixActivity.class,
                    this::isSymbolPrefixExist, unit.prefixes, R.string.error_symbol_prefix_already_exist,
                    this::newPrefix);
        } else super.onItemClick(adapterView, view, position, l);
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
