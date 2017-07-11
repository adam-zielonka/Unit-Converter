package pro.adamzielonka.converter.activities.edit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;
import static pro.adamzielonka.converter.tools.Message.showError;

public abstract class EditActivity extends AppCompatActivity implements IEdit {

    Measure userMeasure;
    ConcreteMeasure concreteMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            onLoad();
        } catch (Exception e) {
            finish();
        }
    }

    void onSave() {
        onSave(true);
    }

    void onSave(boolean reload) {
        try {
            saveMeasure(concreteMeasure, userMeasure);
            if (reload) onReload();
        } catch (Exception e) {
            showError(this, R.string.error_could_not_save_changes);
        }
    }

    //region open and save
    ConcreteMeasure openConcreteMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        return getGson().fromJson(reader, ConcreteMeasure.class);
    }

    Measure openMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        return getGson().fromJson(reader, Measure.class);
    }

    Unit openUnit(String unitName, Measure measure) {
        for (Unit unit : measure.getUnits()) {
            if (unit.getSymbol().equals(unitName))
                return unit;
        }
        return null;
    }

    Prefix openPrefix(String prefixName, Unit unit) {
        for (Prefix prefix : unit.getPrefixes()) {
            if (prefix.getSymbol().equals(prefixName))
                return prefix;
        }
        return null;
    }

    private void saveMeasure(ConcreteMeasure concreteMeasure, Measure userMeasure) throws IOException {
        Gson gson = getGson();
        String concreteFileName = concreteMeasure.getConcreteFileName();
        String userFileName = concreteMeasure.getUserFileName();
        concreteMeasure = userMeasure.getConcreteMeasure(concreteFileName, userFileName);
        saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
        saveToInternal(this, userFileName, gson.toJson(userMeasure));
    }
    //endregion

    //region is symbol exist
    boolean isSymbolPrefixExist(String newName, List<Prefix> prefixes) {
        for (Prefix prefix : prefixes) {
            if (prefix.getSymbol().equals(newName)) return true;
        }
        return false;
    }

    boolean isSymbolUnitExist(String newName, List<Unit> units) {
        for (Unit unit : units) {
            if (unit.getSymbol().equals(newName)) return true;
        }
        return false;
    }
    //endregion
}
