package pro.adamzielonka.converter.activities.edit;

import android.view.View;

import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;

public class EditOrderUnitsActivity extends EditActivity {

    private OrderAdapter orderAdapter;

    @Override
    public void onLoad() throws Exception {
        super.onLoad();
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.getConcreteUnits());
        listView.setAdapter(orderAdapter);
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        orderAdapter.clear();
        orderAdapter.addAll(concreteMeasure.getConcreteUnits());
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
        String find = concreteUnit.getName();
        for (Unit unit : userMeasure.getUnits()) {
            if (unit.getSymbol().equals(find)) {
                unit.setPosition(unit.getPosition() + change);
                return;
            }
            for (Prefix prefix : unit.getPrefixes()) {
                if (find.equals(prefix.getSymbol() + unit.getSymbol())) {
                    prefix.setPosition(prefix.getPosition() + change);
                    return;
                }
            }
        }
    }
}
