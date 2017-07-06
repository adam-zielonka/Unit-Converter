package pro.adamzielonka.converter.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.ConcreteAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.Measures;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.concrete.ConcreteUnit;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.Common.getItself;
import static pro.adamzielonka.converter.tools.FileTools.getFileUri;
import static pro.adamzielonka.converter.tools.FileTools.isExternalStorageWritable;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;
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
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

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

        if (count > 0) setupConverter(DEFAULT_CONVERTER_ID);
        else {
            Intent intent = new Intent(this.getBaseContext(), EmptyActivity.class);
            startActivity(intent);
            finish();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_converter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_delete_converter:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_converter_title)
                        .setMessage(R.string.delete_converter_msg)
                        .setCancelable(true)
                        .setPositiveButton(R.string.delete_converter_yes, (dialog, which) -> {
                            if (getFileStreamPath(measure.getConcreteFileName()).delete() &&
                                    getFileStreamPath(measure.getUserFileName()).delete()) {
                                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton(R.string.delete_converter_no, (dialog, which) -> {
                }).show();
                return true;
            case R.id.menu_save_converter:
                saveToDownloads();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveToDownloads() {
        String[] PERMISSIONS_STORAGE;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            PERMISSIONS_STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        } else {
            PERMISSIONS_STORAGE = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        ActivityCompat.requestPermissions(this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && isExternalStorageWritable()) {
                save();
            } else {
                showError(this, R.string.error_no_permissions_to_write_file);
            }
        }
    }

    private void save() {
        try {
            FileInputStream in = openFileInput(measure.getUserFileName());
            Reader reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            String json = gson.toJson(gson.fromJson(reader, Measure.class));

            OutputStream out = getContentResolver().openOutputStream(getFileUri(measure.getName()));
            if (out != null) {
                out.write(json.getBytes());
                out.close();
                showSuccess(this, R.string.success_save_to_downloads);
            } else showError(this, R.string.error_create_file);
        } catch (Exception e) {
            e.printStackTrace();
            showError(this, R.string.error_create_file);
        }
    }

}
