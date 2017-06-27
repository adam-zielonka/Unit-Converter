package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.units.Measures;
import pro.adamzielonka.converter.units.Units;

public class LoadActivity extends AppCompatActivity {

    private static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        loadConverters();

        Intent converter = new Intent(this.getBaseContext(), ConverterActivity.class);
        startActivity(converter);
        finish();
    }

    private void loadConverters() {
        Field[] fields = R.raw.class.getFields();

        List<Units> unitsList = new ArrayList<>();

        for (Field field : fields) {
            String name = field.getName();
            if (name.contains("converter_")) {
                InputStream raw = getResources().openRawResource(getIdResourceByName("raw", name));
                Reader reader = new BufferedReader(new InputStreamReader(raw));
                Gson gson = new Gson();
                unitsList.add(gson.fromJson(reader, Units.class));
            }
        }

        Measures measures = Measures.getInstance();
        measures.setUnitsList(unitsList);
    }

    private int getIdResourceByName(String defType, String name) {
        return getResources().getIdentifier(name, defType, PACKAGE_NAME);
    }
}
