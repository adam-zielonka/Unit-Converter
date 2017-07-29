package pro.adamzielonka.converter.activities.abstractes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.ListAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.interfaces.IAlert;
import pro.adamzielonka.converter.models.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.models.user.Measure;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;
import pro.adamzielonka.converter.tools.FileTools;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;
import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public abstract class EditActivity extends ListActivity {

    protected Measure userMeasure;
    protected ConcreteMeasure concreteMeasure;
    protected Unit unit;
    protected Prefix prefix;
    protected String language;

    private String measureFileName;
    protected String unitName;
    protected String prefixName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected void onSave() {
        onSave(true);
    }

    protected void onSave(boolean reload) {
        try {
            FileTools.saveMeasure(this, concreteMeasure, userMeasure);
            setResultCode(RESULT_OK);
            if (reload) onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showError(this, R.string.error_could_not_save_changes);
        }
    }

    protected void onLoad() throws Exception {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra(EXTRA_MEASURE_FILE_NAME);
        unitName = intent.getStringExtra("unitName");
        prefixName = intent.getStringExtra("prefixName");
        language = intent.getStringExtra("language");
        super.onLoad();
        onOpen();
    }

    protected void onUpdate() throws Exception {
        onOpen();
    }

    private void onOpen() throws Exception {
        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.userFileName);
        unit = userMeasure != null ? openUnit(unitName, userMeasure) : null;
        prefix = unit != null ? openPrefix(prefixName, unit) : null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            try {
                setResultCode(RESULT_OK);
                onUpdate();
            } catch (Exception e) {
                finish();
            }
        }
    }

    //region open and save
    private ConcreteMeasure openConcreteMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        return getGson().fromJson(reader, ConcreteMeasure.class);
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

    //region isTrueOrFalse
    protected boolean isSymbolPrefixExist(String newName, List<Prefix> prefixes) {
        for (Prefix prefix : prefixes) {
            if (prefix.symbol.equals(newName)) return true;
        }
        return false;
    }

    protected boolean isSymbolUnitExist(String newName, List<Unit> units) {
        for (Unit unit : units) {
            if (unit.symbol.equals(newName)) return true;
        }
        return false;
    }
    //endregion

    protected Intent setEditIntent(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra(EXTRA_MEASURE_FILE_NAME, concreteMeasure.concreteFileName);
        intent.putExtra("unitName", unit != null ? unit.symbol : "");
        intent.putExtra("prefixName", prefix != null ? prefix.symbol : "");
        intent.putExtra("language", language != null ? language : "en");
        return intent;
    }

    //region dialog
    protected void newAlertDialogText(int title, String text, IAlert.ITextAlert alert) {
        EditText editText = getDialogEditText(text);
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            alert.onResult(editText.getText().toString());
            onSave();
        }).show();
    }

    protected void newAlertDialogTextExist(int title, String text, IAlert.IExistTest test, List list, int error, IAlert.ITextAlert alert) {
        EditText editText = getDialogEditText(text);
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            String newText = editText.getText().toString();
            if (!newText.equals(text)) {
                if (!test.onTest(newText, list)) {
                    alert.onResult(newText);
                    onSave();
                } else {
                    showError(this, error);
                }
            }
        }).show();
    }

    protected void newAlertDialogTextCreate(int title, Class<?> intentClass, IAlert.IExistTest test, List list, int error, IAlert.ITextAlert alert) {
        EditText editText = getDialogEditText("");
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            String newText = editText.getText().toString();
            if (!test.onTest(newText, list)) {
                alert.onResult(newText);
                Intent intent = setEditIntent(intentClass);
                onSave();
                startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
            } else {
                showError(this, error);
            }
        }).show();
    }

    protected void newAlertDialogNumber(int title, Double number, IAlert.INumberAlert alert) {
        EditText editText = getDialogEditNumber(number);
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            alert.onResult(stringToDouble(editText.getText().toString()));
            onSave();
        }).show();
    }

    protected void newAlertDialogList(int title, String[] strings, int position, IAlert.IListAlert alert) {
        getAlertDialog(title).setSingleChoiceItems(strings, position, (dialogInterface, i) -> {
            int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
            alert.onResult(selectedPosition);
            dialogInterface.dismiss();
            onSave();
        }).show();
    }

    protected void newAlertDialogAdapter(int title, ListAdapter adapter, IAlert.IListAlert alert) {
        getAlertDialog(title)
                .setAdapter(adapter, (dialogInterface, i) -> {
                    alert.onResult(i);
                    onSave();
                }).show();
    }

    protected void newAlertDialogDelete(int title, IAlert.IVoidAlert alert) {
        getAlertDialogDelete(title, (dialog, which) -> {
            alert.onResult();
            onSave(false);
            onBackPressed();
        }).show();
    }
    //endregion
}
