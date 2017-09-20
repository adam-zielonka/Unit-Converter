package pro.adamzielonka.converter.activities.edit;

import android.view.View;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.models.concrete.CUnit;
import pro.adamzielonka.converter.models.file.Prefix;
import pro.adamzielonka.converter.models.file.Unit;
import pro.adamzielonka.items.Item;

public class EditOrderUnitsActivity extends EditActivity {

    private OrderAdapter orderAdapter;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_order_units);
        super.addItems();
        orderAdapter = new OrderAdapter(getApplicationContext(), cMeasure.cUnits, measure.global, cMeasure.global);
        new Item.Builder(this)
                .setAdapter(orderAdapter)
                .setUpdateAdapter(() -> cMeasure.cUnits)
                .add(itemsView);
    }

    public void setUp(View v) {
        View item = (View) v.getParent();
        int position = itemsView.getPositionForView(item);
        changePosition(orderAdapter.getItem(position), +1);
        itemsView.onSave();
    }

    public void setDown(View v) {
        View item = (View) v.getParent();
        int position = itemsView.getPositionForView(item);
        changePosition(orderAdapter.getItem(position), -1);
        itemsView.onSave();
    }

    private void changePosition(CUnit cUnit, int change) {
        String find = cUnit.name;
        for (Unit unit : measure.units) {
            if (unit.symbol.equals(find)) {
                unit.position += change;
                return;
            }
            for (Prefix prefix : unit.prefixes) {
                if (find.equals(prefix.symbol + unit.symbol)) {
                    prefix.position += change;
                    return;
                }
            }
        }
    }
}
