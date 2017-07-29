package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.PrefixesAdapter;
import pro.adamzielonka.converter.models.user.Prefix;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Message.showError;
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
                EditText editText = getDialogEditText(unit.symbol);
                getAlertDialogSave(R.string.dialog_unit_symbol, editText.getRootView(), (dialog, which) -> {
                    String newName = editText.getText().toString();
                    if (!newName.equals(unitName)) {
                        if (!isSymbolUnitExist(newName, userMeasure.units)) {
                            unit.symbol = newName;
                            unitName = newName;
                            onSave();
                        } else {
                            showError(this, R.string.error_symbol_unit_already_exist);
                        }
                    }
                }).show();

            } else if (view.equals(editDescriptionView)) {
                startActivityForResult(setEditIntent(EditDescriptionActivity.class), REQUEST_EDIT_ACTIVITY);

            } else if (view.equals(editFormulaView)) {
                startActivityForResult(setEditIntent(EditFormulaActivity.class), REQUEST_EDIT_ACTIVITY);

            } else if (view.equals(editExpBaseView)) {
                final NumberPicker numberPicker = new NumberPicker(this);
                numberPicker.setMaxValue(100);
                numberPicker.setValue(Integer.parseInt(doubleToString(unit.expBase)));
                numberPicker.setMinValue(2);

                getAlertDialogSave(R.string.dialog_unit_exponentiation_base, numberPicker, (dialog, which) -> {
                    unit.expBase = 1.0 * numberPicker.getValue();
                    onSave();
                }).show();

            } else if (view.equals(addPrefix)) {
                EditText editText = getDialogEditText("");
                getAlertDialogSave(R.string.dialog_prefix_symbol, editText.getRootView(), (dialog, which) -> {
                    String newPrefixName = editText.getText().toString();
                    if (!isSymbolPrefixExist(newPrefixName, unit.prefixes)) {
                        prefix = new Prefix();
                        prefix.symbol = newPrefixName;
                        unit.prefixes.add(prefix);
                        Intent intent = setEditIntent(EditPrefixActivity.class);
                        onSave();
                        startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
                    } else {
                        showError(this, R.string.error_symbol_prefix_already_exist);
                    }
                }).show();
            }
        }
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
                getAlertDialogDelete(R.string.delete_unit_title, (dialog, which) -> {
                    userMeasure.units.remove(unit);
                    onSave(false);
                    onBackPressed();
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
