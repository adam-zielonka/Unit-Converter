package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.edit.AddMeasureActivity;
import pro.adamzielonka.converter.activities.edit.DetailMeasureActivity;
import pro.adamzielonka.converter.adapters.ConverterAdapter;
import pro.adamzielonka.converter.components.EditNumber;
import pro.adamzielonka.converter.components.theme.ConverterTheme;
import pro.adamzielonka.converter.components.theme.Theme;
import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.concrete.CUnit;
import pro.adamzielonka.converter.tools.Extra;
import pro.adamzielonka.converter.tools.Language;

import static pro.adamzielonka.converter.file.Open.openJSONs;
import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Converter.doConversion;
import static pro.adamzielonka.converter.tools.Menus.getMenuItems;
import static pro.adamzielonka.java.Common.getItself;
import static pro.adamzielonka.java.Number.doubleToString;
import static pro.adamzielonka.java.Number.stringToDouble;

public class ConverterActivity extends AppCompatActivity implements View.OnFocusChangeListener,
        AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private Theme theme;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private EditNumber editNumberFrom;
    private EditNumber editNumberTo;
    private TextView textViewFrom;
    private TextView textViewTo;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;

    private List<CMeasure> measureList;
    private CMeasure cMeasure;
    private int measureID;
    private ConverterAdapter adapter;

    private static final int DEFAULT_MEASURE_ID = 1000;
    private List<MenuItem> menuItems = new ArrayList<>();
    private boolean menuVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = new ConverterTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadMeasures();
    }

    private void loadMeasures() {
        measureList = openJSONs(this, "concrete_", CMeasure.class);

        if (measureList.size() > 0) {
            setupConvertersMenu(navigationView.getMenu());
            setupConverter(getMeasureID());
            setVisibleOptionMenu(true);
        } else {
            setEmptyLayout(R.string.empty_converters);
            setVisibleOptionMenu(false);
        }
    }

    private int getMeasureID() {
        return getIDFromFileName(getIntent().getStringExtra(Extra.MEASURE_FILE_NAME));
    }

    private int getIDFromFileName(String fileName) {
        int i = 0;
        for (CMeasure cMeasure : measureList) {
            if (cMeasure.concreteFileName.equals(fileName))
                return i + DEFAULT_MEASURE_ID;
            i++;
        }
        return DEFAULT_MEASURE_ID;
    }

    private void setupConvertersMenu(Menu menu) {
        Menu convertersMenu = menu.addSubMenu(getString(R.string.nav_converters));
        menuItems.clear();
        int i = 0;
        for (CMeasure measure : measureList) {
            menuItems.add(convertersMenu.add(0, i + DEFAULT_MEASURE_ID, 0,
                    measure.getName(measure.isOwnLang ? measure.ownLang : Language.getLangCode(this))));
            menuItems.get(i++).setCheckable(true);
        }
    }

    //region setup converter
    private void setupConverter(int measureID) {
        try {
            this.measureID = measureID;
            measureList = openJSONs(this, "concrete_", CMeasure.class);
            cMeasure = measureList.get(this.measureID - DEFAULT_MEASURE_ID);
            setConverterTitle();
            navigationView.setCheckedItem(measureID);

            if (cMeasure.cUnits.size() > 0) {

                findConverterViews();
                setConverterListeners();
                setTextFocus();
                setAdapter();
                setSelection();

                clearScreen();
                setConverterLayout();
            } else setEmptyLayout(R.string.empty_units);

        } catch (Exception e) {
            e.printStackTrace();
            if (DEFAULT_MEASURE_ID != measureID) setupConverter(DEFAULT_MEASURE_ID);
            else finish();
        }
    }

    String getLangCode() {
        return cMeasure.isOwnLang ? cMeasure.ownLang : Language.getLangCode(this);
    }

    void setConverterTitle() {
        String title = cMeasure.isOwnName ? cMeasure.ownName : cMeasure.getName(getLangCode());
        setTitle(title);
        menuItems.get(measureID - DEFAULT_MEASURE_ID).setTitle(title);
    }

    void findConverterViews() {
        textViewFrom = findViewById(R.id.textViewFrom);
        textViewTo = findViewById(R.id.textViewTo);

        editNumberFrom = findViewById(R.id.textFrom);
        editNumberTo = findViewById(R.id.textTo);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
    }

    void setTextFocus() {
        editNumberFrom.setTextColor(theme.getTextColor());
        editNumberTo.setTextColor(Color.BLACK);

        editNumberFrom.requestFocus();
    }

    void setConverterListeners() {
        editNumberFrom.setOnFocusChangeListener(this);
        editNumberTo.setOnFocusChangeListener(this);

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);
    }

    ConverterAdapter getAdapter() {
        return new ConverterAdapter(getApplicationContext(),
                measureList.get(measureID - DEFAULT_MEASURE_ID).cUnits,
                getLangCode(),
                cMeasure.global
        );
    }

    void setAdapter() {
        adapter = getAdapter();
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    void setSelection() {
        spinnerFrom.setSelection(cMeasure.displayFrom);
        spinnerTo.setSelection(cMeasure.displayTo);

        CUnit from = adapter.getItem(spinnerFrom.getSelectedItemPosition());
        CUnit to = adapter.getItem(spinnerTo.getSelectedItemPosition());
        textViewFrom.setText(from != null ? cMeasure.getWords(from.description, getLangCode()) : "");
        textViewTo.setText(to != null ? cMeasure.getWords(to.description, getLangCode()) : "");
    }
    //endregion

    //region visibility
    void setConverterLayout() {
        setConverterVisibility(true);
    }

    void setEmptyLayout(@StringRes int text) {
        ((TextView) findViewById(R.id.textEmpty)).setText(text);
        setConverterVisibility(false);
    }

    void setConverterVisibility(boolean visibility) {
        findViewById(R.id.converter_content).setVisibility(visibility ? View.VISIBLE : View.GONE);
        findViewById(R.id.converter_content_empty).setVisibility(visibility ? View.GONE : View.VISIBLE);
    }
    //endregion

    //region events
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus && editNumberTo.equals(view)) {
            editNumberFrom = getItself(editNumberTo, editNumberTo = editNumberFrom);
            spinnerFrom = getItself(spinnerTo, spinnerTo = spinnerFrom);
            textViewFrom = getItself(textViewTo, textViewTo = textViewFrom);
            setTextFocus();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CUnit from = adapter.getItem(spinnerFrom.getSelectedItemPosition());
        CUnit to = adapter.getItem(spinnerTo.getSelectedItemPosition());
        textViewFrom.setText(from != null ? cMeasure.getWords(from.description, getLangCode()) : "");
        textViewTo.setText(to != null ? cMeasure.getWords(to.description, getLangCode()) : "");
        onCalculate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void onCalculate() {
        double result = doConversion(
                stringToDouble(editNumberFrom.getText().toString()),
                adapter.getItem((int) spinnerFrom.getSelectedItemId()),
                adapter.getItem((int) spinnerTo.getSelectedItemId())
        );
        editNumberTo.setText(doubleToString(result));
    }
    //endregion

    //region keyboard
    public void onClick(View v) {
        keyboardAction(v.getId(), v.getTag() != null ? v.getTag().toString() : null);
    }

    public void keyboardAction(int id, String tag) {
        switch (id) {
            case R.id.buttonComa:
                editNumberFrom.appendComma();
                return;
            case R.id.buttonPlusMinus:
                editNumberFrom.changeSign();
                break;
            case R.id.buttonClearOutput:
                editNumberFrom.setText("0");
                break;
            case R.id.buttonDeleteLast:
                editNumberFrom.deleteLast();
                break;
            case R.id.button0:
            case R.id.button1:
            case R.id.button2:
            case R.id.button3:
            case R.id.button4:
            case R.id.button5:
            case R.id.button6:
            case R.id.button7:
            case R.id.button8:
            case R.id.button9:
                editNumberFrom.appendDigit(tag);
                break;
        }
        onCalculate();
    }

    public void clearScreen() {
        keyboardAction(R.id.buttonClearOutput, null);
    }
    //endregion

    //region navigation
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        if (id != measureID) {
            switch (id) {
                case R.id.nav_settings:
                    Intent set = new Intent(this.getBaseContext(), SettingsActivity.class);
                    startActivity(set);
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

    private void setVisibleOptionMenu(boolean visible) {
        menuVisible = visible;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_converter, menu);
        getMenuItems(menu).myForEach(menuItem -> menuItem.setVisible(menuVisible));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_set_converter:
                Intent intent = new Intent(getApplicationContext(), DetailMeasureActivity.class);
                intent.putExtra(Extra.MEASURE_FILE_NAME, cMeasure.concreteFileName);
                startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_EDIT_ACTIVITY && resultCode == RESULT_OK) {
            setupConverter(measureID);
        }
    }
    //endregion

}
