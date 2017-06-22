package pro.adamzielonka.calculator.activities;

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

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.units.UnitsConverter;
import pro.adamzielonka.calculator.units.UnitsList;

public class LoadActivity extends AppCompatActivity {

    private List<UnitsConverter> unitsConverterList;
    private static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        loadConverters();

        Intent calculator = new Intent(this.getBaseContext(), CalculatorActivity.class);
        startActivity(calculator);
        finish();
    }

    private void loadConverters() {
        Field[] fields = R.raw.class.getFields();

        unitsConverterList = new ArrayList<>();

        for (int i = 0; i < fields.length - 1; i++) {
            String name = fields[i].getName();
            if (name.contains("converter_")) {

                InputStream raw = getResources().openRawResource(getIdResourceByName("raw", name));
                Reader reader = new BufferedReader(new InputStreamReader(raw));
                Gson gson = new Gson();
                unitsConverterList.add(gson.fromJson(reader, UnitsConverter.class));
            }
        }

        UnitsList unitsList = UnitsList.getInstance();
        unitsList.setUnitsConverterList(unitsConverterList);
    }

    private int getIdResourceByName(String defType, String name) {
        return getResources().getIdentifier(name, defType, PACKAGE_NAME);
    }

}
