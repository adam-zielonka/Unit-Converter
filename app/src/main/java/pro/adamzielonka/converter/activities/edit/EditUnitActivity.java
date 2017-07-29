package pro.adamzielonka.converter.activities.edit;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.PrefixesAdapter;
import pro.adamzielonka.converter.models.user.Prefix;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class EditUnitActivity extends EditActivity implements ListView.OnItemClickListener {

    private View editSymbolView;
    private View editDescriptionView;
    private View editFormulaView;
    private View editExpBaseView;
    private View addPrefix;

    private PrefixesAdapter prefixesAdapter;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_edit_unit);
        super.onLoad();
        prefixesAdapter = new PrefixesAdapter(getApplicationContext(), unit, userMeasure.global, userMeasure.global);
        listView.setAdapter(prefixesAdapter);
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_unit));
        editSymbolView = listView.addHeaderItem(getString(R.string.list_item_symbol));
        editDescriptionView = listView.addHeaderItem(getString(R.string.list_item_description));
        editFormulaView = listView.addHeaderItem(getString(R.string.list_item_formula));
        editExpBaseView = listView.addHeaderItem(getString(R.string.list_title_exponentiation_base));
        listView.addHeaderTitle(getString(R.string.list_title_prefixes));
        addPrefix = listView.addFooterItem(getString(R.string.list_item_add_prefix));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(editSymbolView, unit.symbol);
        updateView(editDescriptionView, userMeasure.getWords(unit.descriptionPrefix, userMeasure.global) + userMeasure.getWords(unit.description, userMeasure.global));
        updateView(editFormulaView, getFormula(unit.one, unit.shift, unit.shift2, unit.symbol));
        updateView(editExpBaseView, doubleToString(unit.expBase));
        prefixesAdapter.clear();
        prefixesAdapter.addAll(unit.prefixes);
        prefixesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (isAdapterItemClick(position)) {
            prefix = prefixesAdapter.getItem(getAdapterPosition(position));
            startActivityForResult(setEditIntent(EditPrefixActivity.class), REQUEST_EDIT_ACTIVITY);
        } else {
            if (view.equals(editSymbolView)) {
                newAlertDialogTextExist(R.string.dialog_unit_symbol, unit.symbol,
                        this::isSymbolUnitExist, userMeasure.units, R.string.error_symbol_unit_already_exist,
                        newName -> unit.symbol = unitName = newName);

            } else if (view.equals(editDescriptionView)) {
                startActivityForResult(setEditIntent(EditDescriptionActivity.class), REQUEST_EDIT_ACTIVITY);

            } else if (view.equals(editFormulaView)) {
                startActivityForResult(setEditIntent(EditFormulaActivity.class), REQUEST_EDIT_ACTIVITY);

            } else if (view.equals(editExpBaseView)) {
                newAlertDialogNumber(R.string.dialog_unit_exponentiation_base, unit.expBase, number -> unit.expBase = number);

            } else if (view.equals(addPrefix)) {
                newAlertDialogTextCreate(R.string.dialog_prefix_symbol, EditPrefixActivity.class,
                        this::isSymbolPrefixExist, unit.prefixes, R.string.error_symbol_prefix_already_exist,
                        this::newPrefix);
            }
        }
    }

    private void newPrefix(String symbol) {
        prefix = new Prefix();
        prefix.symbol = symbol;
        unit.prefixes.add(prefix);
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
                newAlertDialogDelete(R.string.delete_unit_title, () -> userMeasure.units.remove(unit));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
