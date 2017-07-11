package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.FileNotFoundException;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.PrefixesAdapter;
import pro.adamzielonka.converter.components.MyListView;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class EditUnitActivity extends EditActivity implements ListView.OnItemClickListener {

    private Unit unit;

    private String measureFileName;
    private String unitName;

    private View unitSymbolView;
    private View unitExpBaseView;

    private PrefixesAdapter prefixesAdapter;

    private static final int COUNT_SETTINGS_ITEMS = 6;
    private static final int EDIT_SYMBOL = 1;
    private static final int EDIT_DESCRIPTION = 2;
    private static final int EDIT_FORMULA = 3;
    private static final int EDIT_EXP_BASE = 4;

    @Override
    public void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");
        unitName = intent.getStringExtra("unitName");

        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        prefixesAdapter = new PrefixesAdapter(getApplicationContext(), unit);

        MyListView listView = findViewById(R.id.editListView);
        listView.setAdapter(prefixesAdapter);
        listView.setOnItemClickListener(this);
        listView.setActivity(this);

        listView.addHeaderTitle(getString(R.string.list_title_unit));
        unitSymbolView = listView.addHeaderItem(getString(R.string.list_item_symbol), unit.getSymbol());
        listView.addHeaderItem(getString(R.string.list_item_description), unit.getDescriptionPrefix() + unit.getDescription());
        listView.addHeaderItem(getString(R.string.list_item_formula), getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()));
        unitExpBaseView = listView.addHeaderItem(getString(R.string.list_title_exponentiation_base), doubleToString(unit.getExpBase()));
        listView.addHeaderTitle(getString(R.string.list_title_prefixes));
        listView.addFooterItem(getString(R.string.list_item_add_prefix));
    }

    @Override
    public void onReload() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        ((TextView) unitSymbolView.findViewById(R.id.textSecondary)).setText(unit.getSymbol());
        ((TextView) unitExpBaseView.findViewById(R.id.textSecondary)).setText(doubleToString(unit.getExpBase()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position - COUNT_SETTINGS_ITEMS < 0 || position - COUNT_SETTINGS_ITEMS >= unit.getPrefixes().size()) {
            switch (position) {
                case EDIT_SYMBOL:
                    View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                    final EditText editText = layout.findViewById(R.id.editText);
                    editText.setText(unit.getSymbol());
                    editText.selectAll();
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_unit_symbol)
                            .setView(layout)
                            .setCancelable(true)
                            .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
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
                            }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    }).show();
                    break;
                case EDIT_DESCRIPTION:
                    Intent description = new Intent(getApplicationContext(), EditDescriptionActivity.class);
                    description.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                    description.putExtra("unitName", unit.getSymbol());
                    startActivity(description);
                    break;
                case EDIT_FORMULA:
                    Intent formula = new Intent(getApplicationContext(), EditFormulaActivity.class);
                    formula.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                    formula.putExtra("unitName", unit.getSymbol());
                    startActivity(formula);
                    break;
                case EDIT_EXP_BASE:
                    final NumberPicker numberPicker = new NumberPicker(this);
                    numberPicker.setMaxValue(100);
                    numberPicker.setValue(Integer.parseInt(doubleToString(unit.getExpBase())));
                    numberPicker.setMinValue(2);
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_unit_exponentiation_base)
                            .setView(numberPicker)
                            .setCancelable(true)
                            .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                                unit.setExpBase(1.0 * numberPicker.getValue());
                                onSave();
                            }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    }).show();
                    break;
                default:
                    Prefix newPrefix = new Prefix();
                    String newPrefixName = "";
                    for (int i = 1; isSymbolPrefixExist(newPrefixName, unit.getPrefixes()); i++) {
                        newPrefixName = "" + i;
                    }
                    newPrefix.setSymbol(newPrefixName);
                    unit.getPrefixes().add(newPrefix);
                    onSave(false);
                    Intent intent = new Intent(getApplicationContext(), EditPrefixActivity.class);
                    intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                    intent.putExtra("unitName", unit.getSymbol());
                    intent.putExtra("prefixName", newPrefixName);
                    startActivity(intent);
            }
            return;
        }
        Prefix prefix = prefixesAdapter.getItem(position - COUNT_SETTINGS_ITEMS);
        Intent intent = new Intent(getApplicationContext(), EditPrefixActivity.class);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit.getSymbol());
        intent.putExtra("prefixName", prefix != null ? prefix.getSymbol() : "");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditMeasureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        startActivity(intent);
        finish();
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
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_unit_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                            userMeasure.getUnits().remove(unit);
                            onSave(false);
                            onBackPressed();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
