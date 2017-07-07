package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;

public class EditOrderUnitsActivity extends AppCompatActivity {

    Measure userMeasure;
    ConcreteMeasure concreteMeasure;
    OrderAdapter orderAdapter;
    String measureFileName;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_units);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");
        try {
            load();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

    }

    private void load() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.getUserFileName());
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.getConcreteUnits());
        listView = findViewById(R.id.orderListView);
        listView.setAdapter(orderAdapter);
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


    void reLoad() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.getUserFileName());
        orderAdapter = new OrderAdapter(getApplicationContext(), concreteMeasure.getConcreteUnits());
        listView.setAdapter(orderAdapter);
    }

    public void saveChange() {
        Gson gson = new Gson();
        String concreteFileName = concreteMeasure.getConcreteFileName();
        String userFileName = concreteMeasure.getUserFileName();
        concreteMeasure = userMeasure.getConcreteMeasure(concreteFileName, userFileName);
        try {
            saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
            saveToInternal(this, userFileName, gson.toJson(userMeasure));
            reLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SetUp(View v) {
        View item = (View) v.getParent();
        int position = listView.getPositionForView(item);
        changePos(position, +1);
        saveChange();
    }

    public void SetDown(View v) {
        View item = (View) v.getParent();
        int position = listView.getPositionForView(item);
        changePos(position, -1);
        saveChange();
    }

    public void changePos(int i, int change) {
        String find = orderAdapter.getItem(i).getName();
        for (Unit unit : userMeasure.getUnits()) {
            if (unit.getUnitName().equals(find)) {
                unit.setUnitPosition(unit.getUnitPosition() + change);
                Log.i("POS", "changePos: " + unit.getUnitPosition());
                return;
            }
            for (Prefix prefix : unit.getPrefixes()) {
                if (find.equals(prefix.getPrefixName() + unit.getUnitName())) {
                    prefix.setUnitPosition(prefix.getUnitPosition() + change);
                    Log.i("POS", "changePos: " + prefix.getUnitPosition());
                    return;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(), EditMeasureActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        startActivity(home);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent home = new Intent(getApplicationContext(), EditMeasureActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                startActivity(home);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
