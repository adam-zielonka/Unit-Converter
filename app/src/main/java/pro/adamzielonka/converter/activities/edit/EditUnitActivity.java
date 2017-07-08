package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.PrefixesAdapter;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.ListItems.getItemHeader;
import static pro.adamzielonka.converter.tools.ListItems.getItemNormal;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;
import static pro.adamzielonka.converter.tools.Open.openUnit;

public class EditUnitActivity extends EditActivity implements ListView.OnItemClickListener {

    private String measureFileName;
    private String unitName;
    private Unit unit;
    private View unitSymbolView;
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

        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        prefixesAdapter = new PrefixesAdapter(getApplicationContext(), unit);
        ListView listView = findViewById(R.id.editListView);
        listView.setAdapter(prefixesAdapter);
        listView.setOnItemClickListener(this);

        unitSymbolView = getItemNormal(this, getString(R.string.list_item_symbol), unit.getUnitName());

        listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_unit)), false, false);
        listView.addHeaderView(unitSymbolView, false, true);
        listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_description), unit.getUnitDescriptionFirst() + unit.getUnitDescription()), false, true);
        listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_formula), getFormula(unit.getOne(), unit.getShift(), unit.getShift2())), false, true);
        listView.addHeaderView(getItemNormal(this, getString(R.string.list_title_exponentiation_base), doubleToString(unit.getPrefixBase())), false, true);
        listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_prefixes)), false, false);
        listView.addFooterView(getItemNormal(this, getString(R.string.list_item_add_prefix)), false, true);
    }

    @Override
    public void onReload() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        ((TextView) unitSymbolView.findViewById(R.id.textSecondary)).setText(unit.getUnitName());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position - COUNT_SETTINGS_ITEMS < 0 || position - COUNT_SETTINGS_ITEMS >= unit.getPrefixes().size()) {
            switch (position) {
                case EDIT_SYMBOL:
                    View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                    final EditText editText = layout.findViewById(R.id.editText);
                    editText.setText(unit.getUnitName());
                    editText.selectAll();
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_unit_symbol)
                            .setView(layout)
                            .setCancelable(true)
                            .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                                unit.setUnitName(editText.getText().toString());
                                unitName = editText.getText().toString();
                                saveChange();
                            }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    }).show();
                    break;
                case EDIT_DESCRIPTION:
                    break;
                case EDIT_FORMULA:
                    break;
                case EDIT_EXP_BASE:
                    break;
            }
            return;
        }
        Intent intent = new Intent(getApplicationContext(), EditPrefixActivity.class);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit.getUnitName());
        intent.putExtra("prefixName", prefixesAdapter.getItem(position - COUNT_SETTINGS_ITEMS).getPrefixName());

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(), EditMeasureActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        startActivity(home);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFormula(Double one, Double shift1, Double shift2) {
        String shift1F = shift1 != 0.0 ? (shift1 < 0.0) ? (" - " + doubleToString((-1) * shift1)) : (" + " + doubleToString(shift1)) : "";
        String shift2F = shift2 != 0.0 ? (shift2 < 0.0) ? (" - " + doubleToString((-1) * shift2)) : (" + " + doubleToString(shift2)) : "";
        String oneF = one != 1.0 ? doubleToString(one) + " * " : "";
        return shift1 != 0.0 && one != 1.0 ? oneF + "(" + "Base" + shift1F + ")" + shift2F : oneF + "Base" + shift1F + shift2F;
    }
}
