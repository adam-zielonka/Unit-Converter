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

import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;

public class EditOrderUnitsActivity extends EditActivity {

    private OrderAdapter orderAdapter;
    private String measureFileName;
    private ListView listView;

    @Override
    public void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");

        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.getConcreteUnits());
        listView = findViewById(R.id.editListView);
        listView.setAdapter(orderAdapter);
    }

    @Override
    public void onReload() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
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
            if (unit.getUnitName().equals(find)) {
                unit.setUnitPosition(unit.getUnitPosition() + change);
                return;
            }
            for (Prefix prefix : unit.getPrefixes()) {
                if (find.equals(prefix.getPrefixName() + unit.getUnitName())) {
                    prefix.setUnitPosition(prefix.getUnitPosition() + change);
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
