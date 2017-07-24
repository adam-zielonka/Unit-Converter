package pro.adamzielonka.converter.activities.edit;

import android.view.View;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;

public class EditOrderUnitsActivity extends EditActivity {

    private OrderAdapter orderAdapter;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_edit_order_units);
        super.onLoad();
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.concreteUnits, userMeasure.global, concreteMeasure.global);
        listView.setAdapter(orderAdapter);
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        orderAdapter.clear();
        orderAdapter.addAll(concreteMeasure.concreteUnits);
        orderAdapter.notifyDataSetChanged();
    }

    public void setUp(View v) {
        View item = (View) v.getParent();
        int position = listView.getPositionForView(item);
        changePosition(orderAdapter.getItem(position), +1);
        onSave();
    }

    public void setDown(View v) {
        View item = (View) v.getParent();
        int position = listView.getPositionForView(item);
        changePosition(orderAdapter.getItem(position), -1);
        onSave();
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
