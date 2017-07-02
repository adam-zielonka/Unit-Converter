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
import pro.adamzielonka.converter.units.Units;

public class LoadActivity extends AppCompatActivity {

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
        List<Units> unitsList = new ArrayList<>();

        for (String name : strings) {
            if (name.contains("converter_")) {
                InputStream raw = getAssets().open("converters/" + name);
                Reader reader = new BufferedReader(new InputStreamReader(raw));
                Gson gson = new Gson();
                unitsList.add(gson.fromJson(reader, Units.class));
            }
        }

        for (Units units : unitsList) {
            units.setArrayUnits();
        }

        Measures measures = Measures.getInstance();
        measures.setUnitsList(unitsList);
    }
}
