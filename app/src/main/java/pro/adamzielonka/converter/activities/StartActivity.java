package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.file.Measure;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;
import static pro.adamzielonka.converter.tools.Language.getLangCode;
import static pro.adamzielonka.converter.tools.FileTools.getNewFileInternalName;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;

public class StartActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private final static String prefFirsRun = "v1.1.18-alpha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (!preferences.getBoolean(prefFirsRun, false)) try {
            firstRun();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent oldIntent = getIntent();
        Intent intent = new Intent(this.getBaseContext(), ConverterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_MEASURE_FILE_NAME, oldIntent.getStringExtra(EXTRA_MEASURE_FILE_NAME));
        startActivity(intent);

        finish();
    }

    private void firstRun() throws IOException {
        clearInternalStorage();

        String[] strings = getAssets().list("converters");
        List<Measure> measureList = new ArrayList<>();
        Gson gson = getGson();

        for (String name : strings) {
            if (name.contains("converter_")) {
                InputStream raw = getAssets().open("converters/" + name);
                Reader reader = new BufferedReader(new InputStreamReader(raw));
                measureList.add(gson.fromJson(reader, Measure.class));
            }
        }

        for (Measure measure : measureList) {
            CMeasure cMeasure = measure.getConcreteMeasure();

            String concreteFileName = getNewFileInternalName(this, "concrete_", cMeasure.getName(getLangCode(this)));
            String userFileName = getNewFileInternalName(this, "user_", cMeasure.getName(getLangCode(this)));
            cMeasure.concreteFileName = concreteFileName;
            cMeasure.userFileName = userFileName;
            saveToInternal(this, concreteFileName, gson.toJson(cMeasure));
            saveToInternal(this, userFileName, gson.toJson(measure));
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
