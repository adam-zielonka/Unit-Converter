package pro.adamzielonka.converter.activities.edit;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

import static pro.adamzielonka.converter.tools.Converter.getFormula;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class EditFormulaActivity extends EditActivity implements ListView.OnItemClickListener {

    private View unitFormulaView;
    private View unitEditOneView;
    private View unitEditShift1View;
    private View unitEditShift2View;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_formula_description);
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_formula));
        unitFormulaView = listView.addHeaderItem(getString(R.string.list_item_formula_description));
        unitEditOneView = listView.addHeaderItem(getString(R.string.list_item_formula_one));
        unitEditShift1View = listView.addHeaderItem(getString(R.string.list_item_formula_shift1));
        unitEditShift2View = listView.addHeaderItem(getString(R.string.list_item_formula_shift2));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(unitFormulaView, getFormula(unit.one, unit.shift, unit.shift2, unit.symbol), false);
        updateView(unitEditOneView, doubleToString(unit.one));
        updateView(unitEditShift1View, doubleToString(unit.shift));
        updateView(unitEditShift2View, doubleToString(unit.shift2));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(unitEditOneView)) {
            newAlertDialogNumber(R.string.dialog_formula_one, unit.one, number -> unit.one = number);

        } else if (view.equals(unitEditShift1View)) {
            newAlertDialogNumber(R.string.dialog_formula_shift1, unit.shift, number -> unit.shift = number);

        } else if (view.equals(unitEditShift2View)) {
            newAlertDialogNumber(R.string.dialog_formula_shift2, unit.shift2, number -> unit.shift2 = number);
        }
    }
}
