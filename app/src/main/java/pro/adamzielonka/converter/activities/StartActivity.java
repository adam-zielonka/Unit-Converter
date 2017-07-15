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

import pro.adamzielonka.converter.activities.drawer.DrawerActivity;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.FileTools.getFileInternalName;
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
        Intent intent = new Intent(this.getBaseContext(), DrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", oldIntent.getStringExtra("measureFileName"));
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

        for (Measure userMeasure : measureList) {
            ConcreteMeasure concreteMeasure = userMeasure.getConcreteMeasure();

            String concreteFileName = getFileInternalName(this, "concrete_", concreteMeasure.getName());
            String userFileName = getFileInternalName(this, "user_", concreteMeasure.getName());
            concreteMeasure.setConcreteFileName(concreteFileName);
            concreteMeasure.setUserFileName(userFileName);
            saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
            saveToInternal(this, userFileName, gson.toJson(userMeasure));
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
