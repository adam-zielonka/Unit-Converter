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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.edit.AddMeasureActivity;
import pro.adamzielonka.converter.activities.edit.DetailMeasureActivity;
import pro.adamzielonka.converter.adapters.ConverterAdapter;
import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.concrete.CUnit;
import pro.adamzielonka.converter.tools.Language;
import pro.adamzielonka.converter.tools.theme.ConverterTheme;
import pro.adamzielonka.converter.tools.theme.Theme;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;
import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Converter.doConversion;
import static pro.adamzielonka.converter.tools.FileTools.loadConverters;
import static pro.adamzielonka.lib.Common.getItself;
import static pro.adamzielonka.lib.Number.appendComma;
import static pro.adamzielonka.lib.Number.appendDigit;
import static pro.adamzielonka.lib.Number.changeSign;
import static pro.adamzielonka.lib.Number.deleteLast;
import static pro.adamzielonka.lib.Number.doubleToString;
import static pro.adamzielonka.lib.Number.stringToDouble;

public class ConverterActivity extends AppCompatActivity implements View.OnFocusChangeListener,
        AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private Theme theme;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private EditText editTextFrom;
    private EditText editTextTo;
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
        measureList = loadConverters(this);

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
        return getIDFromFileName(getIntent().getStringExtra(EXTRA_MEASURE_FILE_NAME));
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
            measureList = loadConverters(this);
            cMeasure = measureList.get(this.measureID - DEFAULT_MEASURE_ID);
            setConverterTitle();
            navigationView.setCheckedItem(measureID);

            if (cMeasure.cUnits.size() > 0) {

                findConverterViews();
                setConverterListeners();
                setTextFocus();
                setAdapter();
                setSelection();

                onClear();
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

        editTextFrom = findViewById(R.id.textFrom);
        editTextTo = findViewById(R.id.textTo);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
    }

    void setTextFocus() {
        editTextFrom.setTextColor(theme.getTextColor());
        editTextTo.setTextColor(Color.BLACK);

        editTextFrom.requestFocus();
    }

    void setConverterListeners() {
        editTextFrom.setOnFocusChangeListener(this);
        editTextTo.setOnFocusChangeListener(this);

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);
    }

    void setAdapter() {
        adapter = new ConverterAdapter(getApplicationContext(),
                measureList.get(measureID - DEFAULT_MEASURE_ID).cUnits,
                getLangCode(),
                cMeasure.global
        );

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
        if (hasFocus && editTextTo.equals(view)) {
            editTextFrom = (EditText) getItself(editTextTo, editTextTo = editTextFrom);
            spinnerFrom = (Spinner) getItself(spinnerTo, spinnerTo = spinnerFrom);
            textViewFrom = (TextView) getItself(textViewTo, textViewTo = textViewFrom);
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
                stringToDouble(editTextFrom.getText().toString()),
                adapter.getItem((int) spinnerFrom.getSelectedItemId()),
                adapter.getItem((int) spinnerTo.getSelectedItemId())
        );
        editTextTo.setText(doubleToString(result));
    }
    //endregion

    //region keyboard
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buttonComa:
                onAppendComma();
                break;
            case R.id.buttonPlusMinus:
                onChangeSign();
                break;
            case R.id.buttonClearOutput:
                onClear();
                break;
            case R.id.buttonDeleteLast:
                onClickDeleteLast();
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
                onAppendDigit(v.getTag().toString());
                break;
        }
    }

    public void onAppendDigit(String tag) {
        editTextFrom.setText(appendDigit(editTextFrom.getText().toString(), tag));
        onCalculate();
    }

    public void onAppendComma() {
        editTextFrom.setText(appendComma(editTextFrom.getText().toString()));
    }

    public void onChangeSign() {
        editTextFrom.setText(changeSign(editTextFrom.getText().toString()));
        onCalculate();
    }

    public void onClear() {
        editTextFrom.setText("0");
        onCalculate();
    }

    public void onClickDeleteLast() {
        editTextFrom.setText(deleteLast(editTextFrom.getText().toString()));
        onCalculate();
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
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(menuVisible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_set_converter:
                Intent intent = new Intent(getApplicationContext(), DetailMeasureActivity.class);
                intent.putExtra(EXTRA_MEASURE_FILE_NAME, cMeasure.concreteFileName);
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
