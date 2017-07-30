package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

import static pro.adamzielonka.converter.tools.Converter.getFormula;

public class EditFormulaActivity extends EditActivity {

    @Override
    protected void addItems() {
        setTitle(R.string.title_activity_formula_description);

        addItemTitle(R.string.list_title_formula);
        addItemText(R.string.list_item_formula_description, () -> getFormula(unit.one, unit.shift, unit.shift2, unit.symbol));
        addItemNumber(R.string.list_item_formula_one, () -> unit.one, one -> unit.one = one);
        addItemNumber(R.string.list_item_formula_shift1, () -> unit.shift, shift -> unit.shift = shift);
        addItemNumber(R.string.list_item_formula_shift2, () -> unit.shift2, shift2 -> unit.shift2 = shift2);
    }
}
