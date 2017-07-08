package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.ListItems.getItemHeader;
import static pro.adamzielonka.converter.tools.ListItems.getItemNormal;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;
import static pro.adamzielonka.converter.tools.Open.openPrefix;
import static pro.adamzielonka.converter.tools.Open.openUnit;

public class EditPrefixActivity extends EditActivity {

    private Unit unit;
    private Prefix prefix;
    private static final int COUNT_SETTINGS_ITEMS = 5;

    @Override
    public void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        String measureFileName = intent.getStringExtra("measureFileName");
        String unitName = intent.getStringExtra("unitName");
        String prefixName = intent.getStringExtra("prefixName");

        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        prefix = openPrefix(prefixName, unit);
        ListView listView = findViewById(R.id.editListView);
        listView.setAdapter(new OrderAdapter(this, (new ArrayList<>())));
        listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_prefix)), false, false);
        listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_symbol), prefix.getPrefixName()), false, true);
        listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_description), prefix.getPrefixDescription()), false, true);
        listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_exponent), doubleToString(prefix.getPrefixExponent())), false, true);
    }

    @Override
    public void onReload() throws FileNotFoundException {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditUnitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit.getUnitName());
        startActivity(intent);
        finish();
    }
}
