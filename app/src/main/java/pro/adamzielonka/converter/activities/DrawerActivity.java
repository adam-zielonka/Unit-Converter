package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.ConcreteAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.Measures;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;

import static pro.adamzielonka.converter.tools.Common.getItself;
import static pro.adamzielonka.converter.tools.Number.appendComa;
import static pro.adamzielonka.converter.tools.Number.appendDigit;
import static pro.adamzielonka.converter.tools.Number.changeSign;
import static pro.adamzielonka.converter.tools.Number.deleteLast;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;
import static pro.adamzielonka.converter.tools.UnitConverter.doConversion;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {

    private DrawerLayout drawer;
    private SharedPreferences preferences;
    private NavigationView navigationView;
    private List<ConcreteMeasure> measureList;
    private int converterID;

    private EditText textFrom;
    private EditText textTo;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private ConcreteAdapter concreteAdapter;

    private static final int DEFAULT_CONVERTER_ID = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getConverterStyleID(preferences.getString("theme", "")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Measures measures = Measures.getInstance();
        measureList = measures.getMeasureList();

        setupConvertersMenu(navigationView.getMenu());

        setupConverter(DEFAULT_CONVERTER_ID);
    }

    private void setupConvertersMenu(Menu menu) {
        Menu convertersMenu = menu.addSubMenu(getString(R.string.nav_converters));

        int i = 0;
        for (ConcreteMeasure measure : measureList) {
            MenuItem menuItem = convertersMenu.add(0, i + DEFAULT_CONVERTER_ID, 0, measure.getName());
            menuItem.setCheckable(true);
            i++;
        }
    }

    private void setupConverter(int converterID) {
        try {
            this.converterID = converterID;
            ConcreteMeasure measure = measureList.get(this.converterID - DEFAULT_CONVERTER_ID);

            setTitle(measure.getName());
            navigationView.setCheckedItem(this.converterID);

            textFrom = (EditText) findViewById(R.id.textFrom);
            textTo = (EditText) findViewById(R.id.textTo);

            textFrom.requestFocus();

            textFrom.setOnFocusChangeListener(this);
            textTo.setOnFocusChangeListener(this);

            concreteAdapter = new ConcreteAdapter(getApplicationContext(),
                    measureList.get(this.converterID - DEFAULT_CONVERTER_ID).getConcreteUnits());

            spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
            spinnerTo = (Spinner) findViewById(R.id.spinnerTo);

            spinnerFrom.setAdapter(concreteAdapter);
            spinnerTo.setAdapter(concreteAdapter);

            spinnerFrom.setOnItemSelectedListener(this);
            spinnerTo.setOnItemSelectedListener(this);
            spinnerFrom.setSelection(measure.getDisplayFrom());
            spinnerTo.setSelection(measure.getDisplayTo());

            onClickClear(null);
        } catch (Exception e) {
            setupConverter(DEFAULT_CONVERTER_ID);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus && textTo.equals(view)) {
            textFrom = (EditText) getItself(textTo, textTo = textFrom);
            spinnerFrom = (Spinner) getItself(spinnerTo, spinnerTo = spinnerFrom);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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


    public void onClickComa(@SuppressWarnings("UnusedParameters") View v) {
        textFrom.setText(appendComa(textFrom.getText().toString()));
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
                case R.id.nav_add_converter:
                    Intent addConverter = new Intent(this.getBaseContext(), AddConverterActivity.class);
                    startActivity(addConverter);
                    break;
                default:
                    setupConverter(id);
            }
        }

        return true;
    }
}
