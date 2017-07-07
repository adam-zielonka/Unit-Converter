package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.ListItems.getItemHeader;
import static pro.adamzielonka.converter.tools.ListItems.getItemNormal;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;
import static pro.adamzielonka.converter.tools.Open.openPrefix;
import static pro.adamzielonka.converter.tools.Open.openUnit;

public class EditPrefixActivity extends AppCompatActivity {

    private Measure userMeasure;
    private ConcreteMeasure concreteMeasure;
    private Unit unit;
    private Prefix prefix;
    private static final int COUNT_SETTINGS_ITEMS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prefix);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        String measureFileName = intent.getStringExtra("measureFileName");
        String unitName = intent.getStringExtra("unitName");
        String prefixName = intent.getStringExtra("prefixName");
        try {
            concreteMeasure = openConcreteMeasure(this, measureFileName);
            userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
            unit = openUnit(unitName, userMeasure);
            prefix = openPrefix(prefixName, unit);
            ListView listView = findViewById(R.id.prefixList);
            listView.setAdapter(new OrderAdapter(this, (new ArrayList<>())));
            listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_prefix)), false, false);
            listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_symbol), prefix.getPrefixName()), false, true);
            listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_description), prefix.getPrefixDescription()), false, true);
            listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_exponent), doubleToString(prefix.getPrefixExponent())), false, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
