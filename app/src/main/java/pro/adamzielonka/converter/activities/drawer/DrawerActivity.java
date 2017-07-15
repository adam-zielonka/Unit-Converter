package pro.adamzielonka.converter.activities.drawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.AboutActivity;
import pro.adamzielonka.converter.activities.edit.AddMeasureActivity;
import pro.adamzielonka.converter.activities.edit.EditMeasureActivity;
import pro.adamzielonka.converter.activities.settings.SettingsActivity;
import pro.adamzielonka.converter.adapters.ConcreteAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.Measures;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.concrete.ConcreteUnit;

import static pro.adamzielonka.converter.tools.Common.getItself;
import static pro.adamzielonka.converter.tools.Converter.doConversion;
import static pro.adamzielonka.converter.tools.Number.appendComma;
import static pro.adamzielonka.converter.tools.Number.appendDigit;
import static pro.adamzielonka.converter.tools.Number.changeSign;
import static pro.adamzielonka.converter.tools.Number.deleteLast;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private List<ConcreteMeasure> measureList;
    private int converterID;
    private String themeID;

    private EditText textFrom;
    private EditText textTo;
    private TextView textViewFrom;
    private TextView textViewTo;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private ConcreteAdapter concreteAdapter;
    private ConcreteMeasure measure;

    private static final int DEFAULT_CONVERTER_ID = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        themeID = preferences.getString("theme", "");
        setTheme(Theme.getConverterStyleID(themeID));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Measures measures = Measures.getInstance();
        measureList = measures.getMeasureList();

        int count = setupConvertersMenu(navigationView.getMenu());

        Intent intent = getIntent();
        if (count > 0) setupConverter(getIDFromFileName(intent.getStringExtra("measureFileName")));
        else {
            Intent empty = new Intent(this.getBaseContext(), EmptyActivity.class);
            startActivity(empty);
            finish();
        }
    }

    private int getIDFromFileName(String fileName) {
        int i = 0;
        for (ConcreteMeasure concreteMeasure : measureList) {
            if (concreteMeasure.getConcreteFileName().equals(fileName))
                return i + DEFAULT_CONVERTER_ID;
            i++;
        }
        return DEFAULT_CONVERTER_ID;
    }

    private int setupConvertersMenu(Menu menu) {
        Menu convertersMenu = menu.addSubMenu(getString(R.string.nav_converters));

        int i = 0;
        for (ConcreteMeasure measure : measureList) {
            MenuItem menuItem = convertersMenu.add(0, i + DEFAULT_CONVERTER_ID, 0, measure.getName());
            menuItem.setCheckable(true);
            i++;
        }
        return i;
    }

    private void setupConverter(int converterID) {
        try {
            this.converterID = converterID;
            measure = measureList.get(this.converterID - DEFAULT_CONVERTER_ID);

            setTitle(measure.getName());
            navigationView.setCheckedItem(this.converterID);

            if (measure.getConcreteUnits().size() != 0) {
                textViewFrom = findViewById(R.id.textViewFrom);
                textViewTo = findViewById(R.id.textViewTo);

                textFrom = findViewById(R.id.textFrom);
                textTo = findViewById(R.id.textTo);

                textFrom.setTextColor(getResources().getColor(Theme.getTextColorID(themeID)));
                textTo.setTextColor(Color.BLACK);

                textFrom.requestFocus();

                textFrom.setOnFocusChangeListener(this);
                textTo.setOnFocusChangeListener(this);

                concreteAdapter = new ConcreteAdapter(getApplicationContext(),
                        measureList.get(this.converterID - DEFAULT_CONVERTER_ID).getConcreteUnits());

                spinnerFrom = findViewById(R.id.spinnerFrom);
                spinnerTo = findViewById(R.id.spinnerTo);

                spinnerFrom.setAdapter(concreteAdapter);
                spinnerTo.setAdapter(concreteAdapter);

                spinnerFrom.setOnItemSelectedListener(this);
                spinnerTo.setOnItemSelectedListener(this);
                spinnerFrom.setSelection(measure.getDisplayFrom());
                spinnerTo.setSelection(measure.getDisplayTo());

                ConcreteUnit from = concreteAdapter.getItem(spinnerFrom.getSelectedItemPosition());
                ConcreteUnit to = concreteAdapter.getItem(spinnerTo.getSelectedItemPosition());
                textViewFrom.setText(from != null ? from.getDescription() : "");
                textViewTo.setText(to != null ? to.getDescription() : "");

                textViewFrom.setVisibility(View.VISIBLE);
                textViewTo.setVisibility(View.VISIBLE);
                textTo.setVisibility(View.VISIBLE);
                textFrom.setVisibility(View.VISIBLE);
                spinnerFrom.setVisibility(View.VISIBLE);
                spinnerTo.setVisibility(View.VISIBLE);
                onClickClear(null);
            } else {
                textViewFrom = findViewById(R.id.textViewFrom);
                textViewTo = findViewById(R.id.textViewTo);

                textFrom = findViewById(R.id.textFrom);
                textTo = findViewById(R.id.textTo);

                spinnerFrom = findViewById(R.id.spinnerFrom);
                spinnerTo = findViewById(R.id.spinnerTo);

                textViewFrom.setVisibility(View.GONE);
                textViewTo.setVisibility(View.GONE);
                textTo.setVisibility(View.GONE);
                textFrom.setVisibility(View.GONE);
                spinnerFrom.setVisibility(View.GONE);
                spinnerTo.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            setupConverter(DEFAULT_CONVERTER_ID);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus && textTo.equals(view)) {
            textFrom = (EditText) getItself(textTo, textTo = textFrom);
            spinnerFrom = (Spinner) getItself(spinnerTo, spinnerTo = spinnerFrom);
            textViewFrom = (TextView) getItself(textViewTo, textViewTo = textViewFrom);
            textFrom.setTextColor(getResources().getColor(Theme.getTextColorID(themeID)));
            textTo.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ConcreteUnit from = concreteAdapter.getItem(spinnerFrom.getSelectedItemPosition());
        ConcreteUnit to = concreteAdapter.getItem(spinnerTo.getSelectedItemPosition());
        textViewFrom.setText(from != null ? from.getDescription() : "");
        textViewTo.setText(to != null ? to.getDescription() : "");
        calculate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void calculate() {
        int fromId = (int) spinnerFrom.getSelectedItemId();
        int toId = (int) spinnerTo.getSelectedItemId();
        double result = doConversion(
                stringToDouble(textFrom.getText().toString()),
                concreteAdapter.getItem(fromId),
                concreteAdapter.getItem(toId)
        );
        textTo.setText(doubleToString(result));
    }

    public void onClickDigit(View v) {
        textFrom.setText(appendDigit(textFrom.getText().toString(), v.getTag().toString()));
        calculate();
    }


    public void onClickComma(@SuppressWarnings("UnusedParameters") View v) {
        textFrom.setText(appendComma(textFrom.getText().toString()));
    }


    public void onClickChangeSign(@SuppressWarnings("UnusedParameters") View v) {
        textFrom.setText(changeSign(textFrom.getText().toString()));
        calculate();
    }

    @SuppressWarnings("WeakerAccess")
    public void onClickClear(@SuppressWarnings({"UnusedParameters", "SameParameterValue"}) View v) {
        textFrom.setText("0");
        calculate();
    }

    public void onClickDeleteLast(@SuppressWarnings("UnusedParameters") View v) {
        textFrom.setText(deleteLast(textFrom.getText().toString()));
        calculate();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        if (id != converterID) {
            switch (id) {
                case R.id.nav_about:
                    Intent about = new Intent(this.getBaseContext(), AboutActivity.class);
                    startActivity(about);
                    break;
                case R.id.nav_settings:
                    Intent settings = new Intent(this.getBaseContext(), SettingsActivity.class);
                    startActivity(settings);
                    break;
                case R.id.nav_add_measure:
                    Intent addConverter = new Intent(this.getBaseContext(), AddMeasureActivity.class);
                    startActivity(addConverter);
                    break;
                default:
                    setupConverter(id);
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_converter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_edit_converter:
                Intent intent = new Intent(getApplicationContext(), EditMeasureActivity.class);
                intent.putExtra("measureFileName", measure.getConcreteFileName());
                startActivity(intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
