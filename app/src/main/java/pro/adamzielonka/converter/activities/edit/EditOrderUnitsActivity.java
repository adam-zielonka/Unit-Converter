package pro.adamzielonka.converter.activities.edit;

import android.view.View;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;
import pro.adamzielonka.items.classes.Item;

public class EditOrderUnitsActivity extends EditActivity {

    private OrderAdapter orderAdapter;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_order_units);
        super.addItems();
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.concreteUnits, userMeasure.global, concreteMeasure.global);
        new Item.Builder(this)
                .setAdapter(orderAdapter)
                .setUpdate(() -> concreteMeasure.concreteUnits)
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

    private void changePosition(ConcreteUnit concreteUnit, int change) {
        String find = concreteUnit.name;
        for (Unit unit : userMeasure.units) {
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
