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

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
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
        listView.setActivity(this);

        listView.addHeaderTitle(getString(R.string.list_title_formula));
        unitFormulaView = listView.addHeaderItem(getString(R.string.list_item_formula_description),
                getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()));
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
            final EditText editText = layout.findViewById(R.id.editText);
            editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
            editText.setText(doubleToString(unit.getOne()));
            editText.setSelection(editText.length());
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
            View layoutShift1 = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            final EditText editTextShift1 = layoutShift1.findViewById(R.id.editText);
            editTextShift1.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
            editTextShift1.setText(doubleToString(unit.getShift()));
            editTextShift1.setSelection(editTextShift1.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_formula_shifit1)
                    .setView(layoutShift1)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setShift(stringToDouble(editTextShift1.getText().toString()));
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(unitEditShift2View)) {
            View layoutShift2 = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            final EditText editTextShift2 = layoutShift2.findViewById(R.id.editText);
            editTextShift2.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
            editTextShift2.setText(doubleToString(unit.getShift2()));
            editTextShift2.setSelection(editTextShift2.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_formula_shifit2)
                    .setView(layoutShift2)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setShift2(stringToDouble(editTextShift2.getText().toString()));
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

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
