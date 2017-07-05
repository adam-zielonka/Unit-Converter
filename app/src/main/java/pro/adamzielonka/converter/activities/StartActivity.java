package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.units.Measures;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

public class StartActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        loadConverters();
        Intent intent = new Intent(this.getBaseContext(), DrawerActivity.class);
        startActivity(intent);

        finish();
    }

    private void loadConverters() {
        if (!preferences.getBoolean("firstRun", false)) try {
            firstRun();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] files = getFilesDir().listFiles();

        Gson gson = new Gson();
        List<ConcreteMeasure> concreteMeasureList = new ArrayList<>();
        for (File file : files) {
            if (file.getName().contains("converter_")) {
                try {
                    FileInputStream in = this.openFileInput(file.getName());
                    Reader reader = new BufferedReader(new InputStreamReader(in));
                    concreteMeasureList.add(gson.fromJson(reader, ConcreteMeasure.class));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        Measures measures = Measures.getInstance();
        measures.setMeasureList(concreteMeasureList);
    }

    private void firstRun() throws IOException {
        String[] strings = getAssets().list("converters");
        List<Measure> measureList = new ArrayList<>();
        Gson gson = new Gson();

        for (String name : strings) {
            if (name.contains("converter_")) {
                InputStream raw = getAssets().open("converters/" + name);
                Reader reader = new BufferedReader(new InputStreamReader(raw));
                measureList.add(gson.fromJson(reader, Measure.class));
            }
        }

        List<ConcreteMeasure> concreteMeasureList = new ArrayList<>();
        for (Measure measure : measureList) {
            concreteMeasureList.add(measure.getConcreteMeasure());
        }

        for (ConcreteMeasure measure : concreteMeasureList) {
            String fileName = "converter_" + measure.getName() + ".json";
            measure.setFileName(fileName);
            String json = gson.toJson(measure);
            FileOutputStream out = openFileOutput(fileName, MODE_PRIVATE);
            out.write(json.getBytes());
            out.close();
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstRun", true);
        editor.apply();
    }
}
