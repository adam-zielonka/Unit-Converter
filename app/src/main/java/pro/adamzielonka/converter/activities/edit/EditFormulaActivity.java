package pro.adamzielonka.converter.activities.edit;

import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import pro.adamzielonka.converter.R;

import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public class EditFormulaActivity extends EditActivity implements ListView.OnItemClickListener {

    private View unitFormulaView;
    private View unitEditOneView;
    private View unitEditShift1View;
    private View unitEditShift2View;

    @Override
    public void onLoad() throws FileNotFoundException {
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_formula));
        unitFormulaView = listView.addHeaderItem(getString(R.string.list_item_formula_description),
                getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()),
                false);
        unitEditOneView = listView.addHeaderItem(getString(R.string.list_item_formula_one), doubleToString(unit.getOne()));
        unitEditShift1View = listView.addHeaderItem(getString(R.string.list_item_formula_shift1), doubleToString(unit.getShift()));
        unitEditShift2View = listView.addHeaderItem(getString(R.string.list_item_formula_shift2), doubleToString(unit.getShift2()));
    }

    @Override
    public void onReload() throws FileNotFoundException {
        super.onReload();
        ((TextView) unitFormulaView.findViewById(R.id.textSecondary)).setText(
                getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()));
        ((TextView) unitEditOneView.findViewById(R.id.textSecondary)).setText(doubleToString(unit.getOne()));
        ((TextView) unitEditShift1View.findViewById(R.id.textSecondary)).setText(doubleToString(unit.getShift()));
        ((TextView) unitEditShift2View.findViewById(R.id.textSecondary)).setText(doubleToString(unit.getShift2()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(unitEditOneView)) {
            View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            EditText editText = getDialogEditNumber(layout, unit.getOne());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_formula_one)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setOne(stringToDouble(editText.getText().toString()));
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(unitEditShift1View)) {
            View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            EditText editText = getDialogEditNumber(layout, unit.getShift());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_formula_shifit1)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setShift(stringToDouble(editText.getText().toString()));
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(unitEditShift2View)) {
            View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            EditText editText = getDialogEditNumber(layout, unit.getShift2());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_formula_shifit2)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setShift2(stringToDouble(editText.getText().toString()));
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        }
    }

    @Override
    public void onBackPressed() {
        setResult(resultCode);
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
