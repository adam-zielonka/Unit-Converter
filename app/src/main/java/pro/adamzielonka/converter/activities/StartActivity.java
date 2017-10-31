package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.settings.DecimalSeparator;

import static pro.adamzielonka.converter.tools.Language.getLangCode;
import static pro.adamzielonka.file.Open.openJSON;
import static pro.adamzielonka.file.Save.getNewFileInternalName;
import static pro.adamzielonka.file.Save.saveJSON;

public class StartActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private final static String prefFirsRun = "v1.1.23-alpha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        checkPreferences();

        if (!preferences.getBoolean(prefFirsRun, false)) try {
            firstRun();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this.getBaseContext(), ConverterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    private void checkPreferences(){
        new DecimalSeparator(this);
    }

    private void firstRun() throws IOException {
        clearInternalStorage();

        String[] strings = getAssets().list("converters");
        List<Measure> measureList = new ArrayList<>();

        for (String name : strings) {
            if (name.contains("converter_")) {
                InputStream raw = getAssets().open("converters/" + name);
                measureList.add(openJSON(raw, Measure.class));
            }
        }

        for (Measure measure : measureList) {
            CMeasure cMeasure = measure.getConcreteMeasure();

            String concreteFileName = getNewFileInternalName(this, "concrete_", cMeasure.getName(getLangCode(this)));
            String userFileName = getNewFileInternalName(this, "user_", cMeasure.getName(getLangCode(this)));
            cMeasure.concreteFileName = concreteFileName;
            cMeasure.userFileName = userFileName;
            saveJSON(this, concreteFileName, cMeasure);
            saveJSON(this, userFileName, measure);
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(prefFirsRun, true);
        editor.apply();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void clearInternalStorage() {
        File[] files = getFilesDir().listFiles();
        for (File file : files) getFileStreamPath(file.getName()).delete();
    }
}
