package pro.adamzielonka.converter.activities.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.components.MyListView;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public abstract class EditActivity extends AppCompatActivity {

    Measure userMeasure;
    ConcreteMeasure concreteMeasure;
    Unit unit;
    Prefix prefix;

    private String measureFileName;
    String unitName;
    String prefixName;

    MyListView listView;

    private int resultCode;

    boolean enabledUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultCode = RESULT_CANCELED;
        enabledUpdate = true;

        try {
            onLoad();
            if (enabledUpdate) onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    void onSave() {
        onSave(true);
    }

    void onSave(boolean reload) {
        try {
            saveMeasure(concreteMeasure, userMeasure);
            setResultUpdate();
            if (reload) onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showError(this, R.string.error_could_not_save_changes);
        }
    }

    void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");
        unitName = intent.getStringExtra("unitName");
        prefixName = intent.getStringExtra("prefixName");
        listView = findViewById(R.id.editListView);
        listView.setActivity(this);
        onOpen();
    }

    void onUpdate() throws FileNotFoundException {
        onOpen();
    }

    private void onOpen() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.getUserFileName());
        unit = userMeasure != null ? openUnit(unitName, userMeasure) : null;
        prefix = unit != null ? openPrefix(prefixName, unit) : null;
    }

    private void setResultUpdate() {
        resultCode = RESULT_OK;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            try {
                this.resultCode = RESULT_OK;
                onUpdate();
            } catch (Exception e) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(resultCode);
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
        for (Unit unit : measure.getUnits()) {
            if (unit.getSymbol().equals(unitName))
                return unit;
        }
        return null;
    }

    private Prefix openPrefix(String prefixName, Unit unit) {
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

    //region isTrueOrFalse
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

    boolean isUnderItemClick(int position, int countHeaderItems, int countUnderItems) {
        return (position - countHeaderItems >= 0 && position - countHeaderItems < countUnderItems);
    }
    //endregion

    //region dialog
    EditText getDialogEditText(String text) {
        View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
        EditText editText = layout.findViewById(R.id.editText);
        editText.setText(text);
        editText.setSelection(editText.length());
        return editText;
    }

    EditText getDialogEditNumber(Double number) {
        View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
        EditText editText = layout.findViewById(R.id.editText);
        editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
        editText.setText(doubleToString(number));
        editText.setSelection(editText.length());
        return editText;
    }

    AlertDialog.Builder getAlertDialogSave(int title, View view, DialogInterface.OnClickListener onClickListener) {
        return getAlertDialog(title, onClickListener, R.string.dialog_save).setView(view);
    }

    AlertDialog.Builder getAlertDialogDelete(int title, DialogInterface.OnClickListener onClickListener) {
        return getAlertDialog(title, onClickListener, R.string.dialog_delete);
    }

    private AlertDialog.Builder getAlertDialog(int dialogTitle, DialogInterface.OnClickListener onClickListener, int positiveButtonTitle) {
        return new AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setCancelable(true)
                .setPositiveButton(positiveButtonTitle, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                });
    }
    //endregion

    void updateView(View view, String text) {
        ((TextView) view.findViewById(R.id.textSecondary)).setText(text);
        if (text.equals("")) view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        else view.findViewById(R.id.textSecondary).setVisibility(View.VISIBLE);
    }

    Intent setEditIntent(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit != null ? unit.getSymbol() : "");
        intent.putExtra("prefixName", prefix != null ? prefix.getSymbol() : "");
        return intent;
    }
}
