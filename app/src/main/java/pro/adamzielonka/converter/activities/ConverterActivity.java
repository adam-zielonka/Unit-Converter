package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.edit.AddMeasureActivity;
import pro.adamzielonka.converter.activities.edit.EditMeasureActivity;
import pro.adamzielonka.converter.adapters.ConcreteAdapter;
import pro.adamzielonka.converter.models.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;
import pro.adamzielonka.converter.tools.Theme;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Common.getItself;
import static pro.adamzielonka.converter.tools.Converter.doConversion;
import static pro.adamzielonka.converter.tools.FileTools.loadConverters;
import static pro.adamzielonka.converter.tools.Number.appendComma;
import static pro.adamzielonka.converter.tools.Number.appendDigit;
import static pro.adamzielonka.converter.tools.Number.changeSign;
import static pro.adamzielonka.converter.tools.Number.deleteLast;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public class ConverterActivity extends AppCompatActivity
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
    private ConcreteAdapter adapter;
    private ConcreteMeasure measure;

    private ConstraintLayout converterLayout;
    private ConstraintLayout emptyLayout;

    private boolean hideMenu = false;

    private static final int DEFAULT_CONVERTER_ID = 1000;
    private List<MenuItem> menuItems = new ArrayList<>();

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        themeID = preferences.getString("theme", "");
        setTheme(Theme.getConverterStyleID(themeID));

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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        measureList = loadConverters(this);

        converterLayout = findViewById(R.id.converter_content);
        emptyLayout = findViewById(R.id.converter_content_empty);
        TextView emptyText = findViewById(R.id.textEmpty);

        Intent intent = getIntent();
        if (measureList.size() > 0) {
            emptyText.setText(R.string.empty_units);
            setupConvertersMenu(navigationView.getMenu());
            setupConverter(getIDFromFileName(intent.getStringExtra("measureFileName")));
        } else {
            setEmptyLayout();
            emptyText.setText(R.string.empty_converters);
            hideMenu = true;
            invalidateOptionsMenu();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "converter");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "empty");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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

    private void setupConvertersMenu(Menu menu) {
        Menu convertersMenu = menu.addSubMenu(getString(R.string.nav_converters));
        menuItems.clear();
        int i = 0;
        for (ConcreteMeasure measure : measureList) {
            menuItems.add(convertersMenu.add(0, i + DEFAULT_CONVERTER_ID, 0, measure.getName()));
            menuItems.get(i).setCheckable(true);
            i++;
        }
    }

    //region setup converter
    private void setupConverter(int converterID) {
        try {
            this.converterID = converterID;
            measureList = loadConverters(this);
            measure = measureList.get(this.converterID - DEFAULT_CONVERTER_ID);

            setTitle(measure.getName());
            navigationView.setCheckedItem(this.converterID);
            menuItems.get(this.converterID - DEFAULT_CONVERTER_ID).setTitle(measure.getName());

            if (measure.getConcreteUnits().size() > 0) {

                findConverterViews();
                setConverterListeners();
                setTextFocus();
                setAdapter();
                setSelection();

                onClickClear(null);
                setConverterLayout();
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "converter");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, measure.getName());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "converter");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, measure.getName());
                bundle.putString(FirebaseAnalytics.Param.VALUE, "empty");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                setEmptyLayout();
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (DEFAULT_CONVERTER_ID != converterID) setupConverter(DEFAULT_CONVERTER_ID);
            else finish();
        }
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
        textFrom.setTextColor(getResources().getColor(Theme.getTextColorID(themeID)));
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
                measureList.get(converterID - DEFAULT_CONVERTER_ID).getConcreteUnits());

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    void setSelection() {
        spinnerFrom.setSelection(measure.getDisplayFrom());
        spinnerTo.setSelection(measure.getDisplayTo());

        ConcreteUnit from = adapter.getItem(spinnerFrom.getSelectedItemPosition());
        ConcreteUnit to = adapter.getItem(spinnerTo.getSelectedItemPosition());
        textViewFrom.setText(from != null ? from.getDescription() : "");
        textViewTo.setText(to != null ? to.getDescription() : "");
    }
    //endregion

    //region events
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
        ConcreteUnit from = adapter.getItem(spinnerFrom.getSelectedItemPosition());
        ConcreteUnit to = adapter.getItem(spinnerTo.getSelectedItemPosition());
        textViewFrom.setText(from != null ? from.getDescription() : "");
        textViewTo.setText(to != null ? to.getDescription() : "");
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


    public void onClickComma(@SuppressWarnings("UnusedParameters") View v) {
        textFrom.setText(appendComma(textFrom.getText().toString()));
    }


    public void onClickChangeSign(@SuppressWarnings("UnusedParameters") View v) {
        textFrom.setText(changeSign(textFrom.getText().toString()));
        onCalculate();
    }

    @SuppressWarnings("WeakerAccess")
    public void onClickClear(@SuppressWarnings({"UnusedParameters", "SameParameterValue"}) View v) {
        textFrom.setText("0");
        onCalculate();
    }

    public void onClickDeleteLast(@SuppressWarnings("UnusedParameters") View v) {
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
                case R.id.nav_about:
                    Intent about = new Intent(this.getBaseContext(), AboutActivity.class);
                    startActivity(about);
                    break;
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
            case R.id.menu_edit_converter:
                Intent intent = new Intent(getApplicationContext(), EditMeasureActivity.class);
                intent.putExtra("measureFileName", measure.getConcreteFileName());
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
