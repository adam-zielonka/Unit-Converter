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
import java.util.ArrayList;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.units.user.Unit;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.ListItems.getItemHeader;
import static pro.adamzielonka.converter.tools.ListItems.getItemNormal;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;
import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;
import static pro.adamzielonka.converter.tools.Open.openUnit;

public class EditFormulaActivity extends EditActivity implements ListView.OnItemClickListener {

    private String measureFileName;
    private String unitName;

    private View unitFormulaView;
    private View unitFormulaOneView;
    private View unitFormulaShift1View;
    private View unitFormulaShift2View;

    private Unit unit;

    private static final int EDIT_FORMULA_ONE = 2;
    private static final int EDIT_FORMULA_SHIFT_1 = 3;
    private static final int EDIT_FORMULA_SHIFT_2 = 4;

    @Override
    public void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");
        unitName = intent.getStringExtra("unitName");

        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        ListView listView = findViewById(R.id.editListView);
        listView.setAdapter(new OrderAdapter(this, (new ArrayList<>())));
        listView.setOnItemClickListener(this);

        unitFormulaView = getItemNormal(this, getString(R.string.list_item_formula_description), getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()));
        unitFormulaOneView = getItemNormal(this, getString(R.string.list_item_formula_one), doubleToString(unit.getOne()));
        unitFormulaShift1View = getItemNormal(this, getString(R.string.list_item_formula_shift1), doubleToString(unit.getShift()));
        unitFormulaShift2View = getItemNormal(this, getString(R.string.list_item_formula_shift2), doubleToString(unit.getShift2()));

        listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_formula)), false, false);
        listView.addHeaderView(unitFormulaView, false, false);
        listView.addHeaderView(unitFormulaOneView, false, true);
        listView.addHeaderView(unitFormulaShift1View, false, true);
        listView.addHeaderView(unitFormulaShift2View, false, true);
    }

    @Override
    public void onReload() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        ((TextView) unitFormulaOneView.findViewById(R.id.textSecondary)).setText(doubleToString(unit.getOne()));
        ((TextView) unitFormulaShift1View.findViewById(R.id.textSecondary)).setText(doubleToString(unit.getShift()));
        ((TextView) unitFormulaShift2View.findViewById(R.id.textSecondary)).setText(doubleToString(unit.getShift2()));
        ((TextView) unitFormulaView.findViewById(R.id.textSecondary)).setText(getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case EDIT_FORMULA_ONE:
                View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                final EditText editText = layout.findViewById(R.id.editText);
                editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
                editText.setText(doubleToString(unit.getOne()));
                editText.setSelection(editText.length());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_unit_symbol)
                        .setView(layout)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                            unit.setOne(stringToDouble(editText.getText().toString()));
                            onSave();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                break;
            case EDIT_FORMULA_SHIFT_1:
                View layoutShift1 = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                final EditText editTextShift1 = layoutShift1.findViewById(R.id.editText);
                editTextShift1.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
                editTextShift1.setText(doubleToString(unit.getShift()));
                editTextShift1.setSelection(editTextShift1.length());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_unit_symbol)
                        .setView(layoutShift1)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                            unit.setShift(stringToDouble(editTextShift1.getText().toString()));
                            onSave();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                break;
            case EDIT_FORMULA_SHIFT_2:
                View layoutShift2 = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                final EditText editTextShift2 = layoutShift2.findViewById(R.id.editText);
                editTextShift2.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
                editTextShift2.setText(doubleToString(unit.getShift2()));
                editTextShift2.setSelection(editTextShift2.length());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_unit_symbol)
                        .setView(layoutShift2)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                            unit.setShift2(stringToDouble(editTextShift2.getText().toString()));
                            onSave();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditUnitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit.getSymbol());
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
