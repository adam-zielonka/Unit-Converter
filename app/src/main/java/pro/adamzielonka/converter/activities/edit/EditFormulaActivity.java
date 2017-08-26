package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.items.Item;

import static pro.adamzielonka.converter.tools.Converter.getFormula;

public class EditFormulaActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_formula);
        super.addItems();

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_formula)
                .setTitle(R.string.list_item_formula_description)
                .setUpdate(() -> getFormula(unit.one, unit.shift, unit.shift2, unit.symbol))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_formula_one)
                .setUpdate(() -> unit.one)
                .setAction((Double one) -> unit.one = one)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_formula_shift1)
                .setUpdate(() -> unit.shift)
                .setAction((Double shift) -> unit.shift = shift)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_formula_shift2)
                .setUpdate(() -> unit.shift2)
                .setAction((Double shift2) -> unit.shift2 = shift2)
                .add(itemsView);
    }
}
