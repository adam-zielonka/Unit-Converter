package pro.adamzielonka.converter.activities.abstractes;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.file.Open;
import pro.adamzielonka.converter.file.Save;
import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.models.file.Prefix;
import pro.adamzielonka.converter.models.file.Unit;
import pro.adamzielonka.converter.tools.Extra;
import pro.adamzielonka.items.ItemsView;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.java.Common.findElement;

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
            saveMeasure();
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
        measureFileName = intent.getStringExtra(Extra.MEASURE_FILE_NAME);
        unitName = intent.getStringExtra(Extra.UNIT_NAME);
        prefixName = intent.getStringExtra(Extra.PREFIX_NAME);
        language = intent.getStringExtra(Extra.LANGUAGE);
        translation = intent.getStringExtra(Extra.TRANSLATION);
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
        return Open.openJSON(this, fileName, CMeasure.class);
    }

    private Measure openMeasure(String fileName) throws FileNotFoundException {
        return Open.openJSON(this, fileName, Measure.class);
    }

    private Unit openUnit(String unitName, Measure measure) {
        return findElement(unitName, measure.units);
    }

    private Prefix openPrefix(String prefixName, Unit unit) {
        return findElement(prefixName, unit.prefixes);
    }

    public void saveMeasure() throws IOException {
        cMeasure = measure.getConcreteMeasure(
                cMeasure.concreteFileName, cMeasure.userFileName,
                cMeasure.isOwnName, cMeasure.ownName,
                cMeasure.isOwnLang, cMeasure.ownLang, cMeasure.newLangs
        );
        Save.saveJSON(this, cMeasure.concreteFileName, cMeasure);
        Save.saveJSON(this, cMeasure.userFileName, measure);
    }

    public Uri getFileUri(String name) {
        String fileName = "converter_" + name.toLowerCase() + ".json";
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        for (int i = 1; file.exists(); i++) {
            fileName = "converter_" + name.toLowerCase() + "_" + i + ".json";
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), fileName);
        }
        return Uri.parse(file.toURI().toString());
    }
    //endregion

    protected void startEditActivity(Class<?> cls) {
        startActivityForResult(setEditIntent(cls), REQUEST_EDIT_ACTIVITY);
    }

    protected Intent setEditIntent(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra(Extra.MEASURE_FILE_NAME, cMeasure.concreteFileName);
        intent.putExtra(Extra.UNIT_NAME, unit != null ? unit.symbol : "");
        intent.putExtra(Extra.PREFIX_NAME, prefix != null ? prefix.symbol : "");
        intent.putExtra(Extra.LANGUAGE, language != null ? language : "en");
        return intent;
    }
}
