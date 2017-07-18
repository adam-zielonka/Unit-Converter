package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.io.FileNotFoundException;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.PrefixesAdapter;
import pro.adamzielonka.converter.units.user.Prefix;

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
    public void onLoad() throws FileNotFoundException {
        super.onLoad();
        prefixesAdapter = new PrefixesAdapter(getApplicationContext(), unit);
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
    public void onUpdate() throws FileNotFoundException {
        super.onUpdate();
        updateView(editSymbolView, unit.getSymbol());
        updateView(editDescriptionView, unit.getFullDescription());
        updateView(editFormulaView, getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()));
        updateView(editExpBaseView, doubleToString(unit.getExpBase()));
        prefixesAdapter.clear();
        prefixesAdapter.addAll(unit.getPrefixes());
        prefixesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (isUnderItemClick(position, listView.getCountHeaderItems(), unit.getPrefixes().size())) {
            prefix = prefixesAdapter.getItem(position - listView.getCountHeaderItems());
            startActivityForResult(setEditIntent(EditPrefixActivity.class), REQUEST_EDIT_ACTIVITY);
        } else {
            if (view.equals(editSymbolView)) {
                EditText editText = getDialogEditText(unit.getSymbol());
                getAlertDialogSave(R.string.dialog_unit_symbol, editText.getRootView(), (dialog, which) -> {
                    String newName = editText.getText().toString();
                    if (!newName.equals(unitName)) {
                        if (!isSymbolUnitExist(newName, userMeasure.getUnits())) {
                            unit.setSymbol(newName);
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
                numberPicker.setValue(Integer.parseInt(doubleToString(unit.getExpBase())));
                numberPicker.setMinValue(2);

                getAlertDialogSave(R.string.dialog_unit_exponentiation_base, numberPicker, (dialog, which) -> {
                    unit.setExpBase(1.0 * numberPicker.getValue());
                    onSave();
                }).show();

            } else if (view.equals(addPrefix)) {
                EditText editText = getDialogEditText("");
                getAlertDialogSave(R.string.dialog_prefix_symbol, editText.getRootView(), (dialog, which) -> {
                    String newPrefixName = editText.getText().toString();
                    if (!EditUnitActivity.this.isSymbolPrefixExist(newPrefixName, unit.getPrefixes())) {
                        prefix = new Prefix();
                        prefix.setSymbol(newPrefixName);
                        unit.getPrefixes().add(prefix);
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
                    userMeasure.getUnits().remove(unit);
                    onSave(false);
                    onBackPressed();
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
