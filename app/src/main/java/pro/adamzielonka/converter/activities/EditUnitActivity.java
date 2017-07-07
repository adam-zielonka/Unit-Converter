package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.PrefixesAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.ListItems.getItemHeader;
import static pro.adamzielonka.converter.tools.ListItems.getItemNormal;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class EditUnitActivity extends AppCompatActivity {
    Measure userMeasure;
    ConcreteMeasure concreteMeasure;
    Unit unit;
    private static final int COUNT_SETTINGS_ITEMS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_unit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        Intent intent = getIntent();
        String measureFileName = intent.getStringExtra("measureFileName");
        String unitName = intent.getStringExtra("unitName");
        try {
            concreteMeasure = openConcreteMeasure(measureFileName);
            userMeasure = openMeasure(concreteMeasure.getUserFileName());
            unit = openUnit(unitName, userMeasure);
            PrefixesAdapter prefixesAdapter = new PrefixesAdapter(getApplicationContext(), unit);
            ListView listView = findViewById(R.id.prefixList);
            listView.setAdapter(prefixesAdapter);
            listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_unit)), false, false);
            listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_symbol), unit.getUnitName()), false, true);
            listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_description), unit.getUnitDescriptionFirst() + unit.getUnitDescription()), false, true);
            listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_formula), getFormula(unit.getOne(), unit.getShift(), unit.getShift2())), false, true);
            listView.addHeaderView(getItemNormal(this, getString(R.string.list_title_exponentiation_base), doubleToString(unit.getPrefixBase())), false, true);
            listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_prefixes)), false, false);

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

    private ConcreteMeasure openConcreteMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        return gson.fromJson(reader, ConcreteMeasure.class);
    }

    private Measure openMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        return gson.fromJson(reader, Measure.class);
    }

    private Unit openUnit(String unitName, Measure userMeasure) {
        for (Unit unit : userMeasure.getUnits()) {
            if (unit.getUnitName().equals(unitName))
                return unit;
        }
        return null;
    }

    private String getFormula(Double one, Double shift1, Double shift2) {
        String shift1F = shift1 != 0.0 ? (shift1 < 0.0) ? (" - " + doubleToString((-1) * shift1)) : (" + " + doubleToString(shift1)) : "";
        String shift2F = shift2 != 0.0 ? (shift2 < 0.0) ? (" - " + doubleToString((-1) * shift2)) : (" + " + doubleToString(shift2)) : "";
        String oneF = one != 1.0 ? doubleToString(one) + " * " : "";
        return shift1 != 0.0 && one != 1.0 ? oneF + "(" + "Base" + shift1F + ")" + shift2F : oneF + "Base" + shift1F + shift2F;
    }

}
