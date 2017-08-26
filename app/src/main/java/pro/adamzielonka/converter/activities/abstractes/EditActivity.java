package pro.adamzielonka.converter.activities.abstractes;

import android.content.Intent;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.models.file.Prefix;
import pro.adamzielonka.converter.models.file.Unit;
import pro.adamzielonka.converter.tools.FileTools;
import pro.adamzielonka.items.ItemsView;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;
import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.Message.showError;

public abstract class EditActivity extends ListActivity
        implements ItemsView.OnItemsSave, ItemsView.OnItemsUpdate {

    protected Measure measure;
    protected CMeasure cMeasure;
    protected Unit unit;
    protected Prefix prefix;
    protected String language;
    protected String translation;

    private String measureFileName;
    protected String unitName;
    protected String prefixName;

    @Override
    public void onSave() {
        try {
            FileTools.saveMeasure(this, cMeasure, measure);
            setResultCode(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
            showError(this, R.string.error_could_not_save_changes);
        }
    }

    @Override
    public void addItems() {
        itemsView.setOnItemsUpdate(this);
        itemsView.setOnItemsSave(this);
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra(EXTRA_MEASURE_FILE_NAME);
        unitName = intent.getStringExtra("unitName");
        prefixName = intent.getStringExtra("prefixName");
        language = intent.getStringExtra("language");
        translation = intent.getStringExtra("translation");
        onUpdate();
    }

    @Override
    public void onUpdate() {
        try {
            onOpen();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void onOpen() throws Exception {
        cMeasure = measureFileName != null ? openConcreteMeasure(measureFileName) : null;
        measure = cMeasure != null ? openMeasure(cMeasure.userFileName) : null;
        unit = measure != null ? openUnit(unitName, measure) : null;
        prefix = unit != null ? openPrefix(prefixName, unit) : null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            try {
                setResultCode(RESULT_OK);
                itemsView.onUpdate();
            } catch (Exception e) {
                finish();
            }
        }
    }

    //region open and save
    private CMeasure openConcreteMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        return getGson().fromJson(reader, CMeasure.class);
    }

    private Measure openMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        return getGson().fromJson(reader, Measure.class);
    }

    private Unit openUnit(String unitName, Measure measure) {
        for (Unit unit : measure.units) {
            if (unit.symbol.equals(unitName))
                return unit;
        }
        return null;
    }

    private Prefix openPrefix(String prefixName, Unit unit) {
        for (Prefix prefix : unit.prefixes) {
            if (prefix.symbol.equals(prefixName))
                return prefix;
        }
        return null;
    }
    //endregion

    protected void startEditActivity(Class<?> cls) {
        startActivityForResult(setEditIntent(cls), REQUEST_EDIT_ACTIVITY);
    }

    protected Intent setEditIntent(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra(EXTRA_MEASURE_FILE_NAME, cMeasure.concreteFileName);
        intent.putExtra("unitName", unit != null ? unit.symbol : "");
        intent.putExtra("prefixName", prefix != null ? prefix.symbol : "");
        intent.putExtra("language", language != null ? language : "en");
        return intent;
    }
}
