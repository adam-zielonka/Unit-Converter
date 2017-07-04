package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            loadConverters();
            Intent intent = new Intent(this.getBaseContext(), DrawerActivity.class);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();
    }

    private void loadConverters() throws IOException {
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

        Measures measures = Measures.getInstance();
        measures.setMeasureList(concreteMeasureList);
    }
}
