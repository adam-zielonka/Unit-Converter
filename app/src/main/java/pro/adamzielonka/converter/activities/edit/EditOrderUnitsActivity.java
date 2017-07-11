package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.FileNotFoundException;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.units.concrete.ConcreteUnit;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

public class EditOrderUnitsActivity extends EditActivity {

    private OrderAdapter orderAdapter;
    private ListView listView;

    @Override
    public void onLoad() throws FileNotFoundException {
        super.onLoad();
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.getConcreteUnits());
        listView = findViewById(R.id.editListView);
        listView.setAdapter(orderAdapter);
    }

    @Override
    public void onReload() throws FileNotFoundException {
        super.onReload();
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.getConcreteUnits());
        listView.setAdapter(orderAdapter);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditMeasureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
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
