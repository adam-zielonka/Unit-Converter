package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import pro.adamzielonka.converter.activities.edit.SetMeasureActivity;
import pro.adamzielonka.converter.adapters.ConcreteAdapter;
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

public class ConverterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private List<CMeasure> measureList;
    private int converterID;
    private Theme theme;

    private EditText textFrom;
    private EditText textTo;
    private TextView textViewFrom;
    private TextView textViewTo;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private ConcreteAdapter adapter;
    private CMeasure cMeasure;

    private ConstraintLayout converterLayout;
    private ConstraintLayout emptyLayout;

    private boolean hideMenu = false;

    private static final int DEFAULT_CONVERTER_ID = 1000;
    private List<MenuItem> menuItems = new ArrayList<>();

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

        measureList = loadConverters(this);

        converterLayout = findViewById(R.id.converter_content);
        emptyLayout = findViewById(R.id.converter_content_empty);
        TextView emptyText = findViewById(R.id.textEmpty);

        if (measureList.size() > 0) {
            emptyText.setText(R.string.empty_units);
            setupConvertersMenu(navigationView.getMenu());
            setupConverter(getIDFromFileName(getIntent().getStringExtra(EXTRA_MEASURE_FILE_NAME)));
        } else {
            setEmptyLayout();
            emptyText.setText(R.string.empty_converters);
            hideMenu = true;
            invalidateOptionsMenu();
        }
    }

    private int getIDFromFileName(String fileName) {
        int i = 0;
        for (CMeasure cMeasure : measureList) {
            if (cMeasure.concreteFileName.equals(fileName))
                return i + DEFAULT_CONVERTER_ID;
            i++;
        }
        return DEFAULT_CONVERTER_ID;
    }

    private void setupConvertersMenu(Menu menu) {
        Menu convertersMenu = menu.addSubMenu(getString(R.string.nav_converters));
        menuItems.clear();
        int i = 0;
        for (CMeasure measure : measureList) {
            menuItems.add(convertersMenu.add(0, i + DEFAULT_CONVERTER_ID, 0,
                    measure.getName(measure.isOwnLang ? measure.ownLang : Language.getLangCode(this))));
            menuItems.get(i).setCheckable(true);
            i++;
        }
    }

    //region setup converter
    private void setupConverter(int converterID) {
        try {
            this.converterID = converterID;
            measureList = loadConverters(this);
            cMeasure = measureList.get(this.converterID - DEFAULT_CONVERTER_ID);
            setConverterTitle();
            navigationView.setCheckedItem(converterID);

            if (cMeasure.cUnits.size() > 0) {

                findConverterViews();
                setConverterListeners();
                setTextFocus();
                setAdapter();
                setSelection();

                onClear();
                setConverterLayout();
            } else {
                setEmptyLayout();
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (DEFAULT_CONVERTER_ID != converterID) setupConverter(DEFAULT_CONVERTER_ID);
            else finish();
        }
    }

    String getLangCode() {
        return cMeasure.isOwnLang ? cMeasure.ownLang : Language.getLangCode(this);
    }

    void setConverterTitle() {
        String title = cMeasure.isOwnName ? cMeasure.ownName : cMeasure.getName(getLangCode());
        setTitle(title);
        menuItems.get(converterID - DEFAULT_CONVERTER_ID).setTitle(title);
    }

    void setConverterLayout() {
        converterLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    void setEmptyLayout() {
        converterLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    void findConverterViews() {
        textViewFrom = findViewById(R.id.textViewFrom);
        textViewTo = findViewById(R.id.textViewTo);

        textFrom = findViewById(R.id.textFrom);
        textTo = findViewById(R.id.textTo);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
    }

    void setTextFocus() {
        textFrom.setTextColor(theme.getTextColor());
        textTo.setTextColor(Color.BLACK);

        textFrom.requestFocus();
    }

    void setConverterListeners() {
        textFrom.setOnFocusChangeListener(this);
        textTo.setOnFocusChangeListener(this);

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);
    }

    void setAdapter() {
        adapter = new ConcreteAdapter(getApplicationContext(),
                measureList.get(converterID - DEFAULT_CONVERTER_ID).cUnits,
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

    //region events
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus && textTo.equals(view)) {
            textFrom = (EditText) getItself(textTo, textTo = textFrom);
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
                stringToDouble(textFrom.getText().toString()),
                adapter.getItem((int) spinnerFrom.getSelectedItemId()),
                adapter.getItem((int) spinnerTo.getSelectedItemId())
        );
        textTo.setText(doubleToString(result));
    }
    //endregion

    //region clicks
    public void onClickDigit(View v) {
        textFrom.setText(appendDigit(textFrom.getText().toString(), v.getTag().toString()));
        onCalculate();
    }

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
        }
    }

    public void onAppendComma() {
        textFrom.setText(appendComma(textFrom.getText().toString()));
    }

    public void onChangeSign() {
        textFrom.setText(changeSign(textFrom.getText().toString()));
        onCalculate();
    }

    public void onClear() {
        textFrom.setText("0");
        onCalculate();
    }

    public void onClickDeleteLast() {
        textFrom.setText(deleteLast(textFrom.getText().toString()));
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

        if (id != converterID) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_converter, menu);
        if (hideMenu) {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_set_converter:
                Intent intent = new Intent(getApplicationContext(), SetMeasureActivity.class);
                intent.putExtra(EXTRA_MEASURE_FILE_NAME, cMeasure.concreteFileName);
                startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_EDIT_ACTIVITY && resultCode == RESULT_OK) {
            setupConverter(converterID);
        }
    }
    //endregion

}
