package pro.adamzielonka.converter.activities.edit;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public class EditFormulaActivity extends EditActivity implements ListView.OnItemClickListener {

    private View unitFormulaView;
    private View unitEditOneView;
    private View unitEditShift1View;
    private View unitEditShift2View;

    @Override
    public void onLoad() throws Exception {
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_formula));
        unitFormulaView = listView.addHeaderItemNotSelectable(getString(R.string.list_item_formula_description));
        unitEditOneView = listView.addHeaderItem(getString(R.string.list_item_formula_one));
        unitEditShift1View = listView.addHeaderItem(getString(R.string.list_item_formula_shift1));
        unitEditShift2View = listView.addHeaderItem(getString(R.string.list_item_formula_shift2));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(unitFormulaView, getFormula(unit.getOne(), unit.getShift(), unit.getShift2(), unit.getSymbol()));
        updateView(unitEditOneView, doubleToString(unit.getOne()));
        updateView(unitEditShift1View, doubleToString(unit.getShift()));
        updateView(unitEditShift2View, doubleToString(unit.getShift2()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(unitEditOneView)) {
            EditText editText = getDialogEditNumber(unit.getOne());
            getAlertDialogSave(R.string.dialog_formula_one, editText.getRootView(), (dialog, which) -> {
                unit.setOne(stringToDouble(editText.getText().toString()));
                onSave();
            }).show();

        } else if (view.equals(unitEditShift1View)) {
            EditText editText = getDialogEditNumber(unit.getShift());
            getAlertDialogSave(R.string.dialog_formula_shift1, editText.getRootView(), (dialog, which) -> {
                unit.setShift(stringToDouble(editText.getText().toString()));
                onSave();
            }).show();

        } else if (view.equals(unitEditShift2View)) {
            EditText editText = getDialogEditNumber(unit.getShift2());
            getAlertDialogSave(R.string.dialog_formula_shift2, editText.getRootView(), (dialog, which) -> {
                unit.setShift2(stringToDouble(editText.getText().toString()));
                onSave();
            }).show();
        }
    }
}
