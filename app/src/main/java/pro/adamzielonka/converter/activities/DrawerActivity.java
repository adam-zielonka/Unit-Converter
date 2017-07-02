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
import pro.adamzielonka.converter.adapters.UnitsAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.Measures;
import pro.adamzielonka.converter.units.Units;

import static pro.adamzielonka.converter.tools.Number.appendComa;
import static pro.adamzielonka.converter.tools.Number.appendDigit;
import static pro.adamzielonka.converter.tools.Number.changeSign;
import static pro.adamzielonka.converter.tools.Number.convertDoubleToString;
import static pro.adamzielonka.converter.tools.Number.convertStringToDouble;
import static pro.adamzielonka.converter.tools.Number.deleteLast;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private SharedPreferences preferences;
    private NavigationView navigationView;
    private List<Units> unitsList;
    private int mItemId;

    private EditText textFrom;
    private EditText textTo;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Units converter;
    private String[][] arrayUnits;
    private UnitsAdapter unitsAdapter;

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

        createConvertersMenu();

        converterSetUp(1000);
    }

    private void converterSetUp(int converterNavId) {
        try {
            mItemId = converterNavId;
            converter = unitsList.get(mItemId - 1000);

            setTitle(converter.getName());
            navigationView.setCheckedItem(mItemId);

            arrayUnits = unitsList.get(mItemId - 1000).getArrayUnits();

            textFrom = (EditText) findViewById(R.id.textFrom);
            textTo = (EditText) findViewById(R.id.textTo);

            textFrom.setOnFocusChangeListener(mResultOnClickListener);
            textTo.setOnFocusChangeListener(mResultOnClickListener);

            unitsAdapter = new UnitsAdapter(getApplicationContext(), arrayUnits);

            spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
            spinnerTo = (Spinner) findViewById(R.id.spinnerTo);

            spinnerFrom.setAdapter(unitsAdapter);
            spinnerTo.setAdapter(unitsAdapter);

            spinnerFrom.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
            spinnerTo.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
            spinnerFrom.setSelection(converter.getDisplayFrom());
            spinnerTo.setSelection(converter.getDisplayTo());

            onClickClear(null);
        } catch (Exception e) {
            converterSetUp(1000);
        }
    }

    private final View.OnFocusChangeListener mResultOnClickListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (textTo.equals(v)) {
                    swapTexts();
                    swapSpinners();
                }
            }
        }
    };

    private void swapTexts() {
        EditText textTemp = textFrom;
        textFrom = textTo;
        textTo = textTemp;
    }

    private void swapSpinners() {
        Spinner spinnerTemp = spinnerFrom;
        spinnerFrom = spinnerTo;
        spinnerTo = spinnerTemp;
    }

    private final AdapterView.OnItemSelectedListener mSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            calculateAndPrintResult();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };

    private void calculateAndPrintResult() {
        int fromId = (int) spinnerFrom.getSelectedItemId();
        int toId = (int) spinnerTo.getSelectedItemId();
        double result = converter.calculate(
                convertStringToDouble(textFrom.getText().toString()),
                unitsAdapter.getItemName(fromId),
                unitsAdapter.getItemName(toId)
        );
        textTo.setText(convertDoubleToString(result));
    }

    public void onClickDigit(View v) {
        textFrom.setText(appendDigit(textFrom.getText().toString(), v.getTag().toString()));
        calculateAndPrintResult();
    }

    public void onClickComa(View v) {
        textFrom.setText(appendComa(textFrom.getText().toString()));
    }

    public void onClickChangeSign(View v) {
        textFrom.setText(changeSign(textFrom.getText().toString()));
        calculateAndPrintResult();
    }

    public void onClickClear(View v) {
        textFrom.setText("0");
        calculateAndPrintResult();
    }

    public void onClickDeleteLast(View v) {
        textFrom.setText(deleteLast(textFrom.getText().toString()));
        calculateAndPrintResult();
    }

    private void createConvertersMenu() {
        Menu menu = navigationView.getMenu();
        Menu convertersMenu = menu.addSubMenu(getString(R.string.nav_converters));

        Measures measures = Measures.getInstance();
        this.unitsList = measures.getUnitsList();

        int i = 0;
        for (Units units : this.unitsList) {
            MenuItem menuItem = convertersMenu.add(0, i + 1000, 0, units.getName());
            menuItem.setCheckable(true);
            i++;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if (id != mItemId) {
            switch (id) {
                case R.id.nav_settings:
                    Intent settings = new Intent(this.getBaseContext(), SettingsActivity.class);
                    startActivity(settings);
                    break;
                default:
                    converterSetUp(id);
            }
        }

        return true;
    }
}
