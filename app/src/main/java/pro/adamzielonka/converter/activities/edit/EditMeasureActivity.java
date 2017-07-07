package pro.adamzielonka.converter.activities.edit;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.StartActivity;
import pro.adamzielonka.converter.adapters.UnitsAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.concrete.ConcreteUnit;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.FileTools.getFileUri;
import static pro.adamzielonka.converter.tools.FileTools.isExternalStorageWritable;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;
import static pro.adamzielonka.converter.tools.ListItems.getItemHeader;
import static pro.adamzielonka.converter.tools.ListItems.getItemNormal;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;
import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;

public class EditMeasureActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    private Measure userMeasure;
    private ConcreteMeasure concreteMeasure;
    private UnitsAdapter unitsAdapter;
    private String measureFileName;
    private ListView listView;
    private View measureNameView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int COUNT_SETTINGS_ITEMS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_measure);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");
        try {
            load();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void load() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unitsAdapter = new UnitsAdapter(getApplicationContext(), userMeasure.getUnits());
        listView = findViewById(R.id.unitsList);
        listView.setAdapter(unitsAdapter);
        listView.setOnItemClickListener(this);
        measureNameView = getItemNormal(this, getString(R.string.list_item_name), userMeasure.getName());

        listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_Measure)), false, false);
        listView.addHeaderView(measureNameView, false, true);
        listView.addHeaderView(getItemNormal(this, getString(R.string.list_item_units_order), getUnitsOrder()), false, true);
        listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_units)), false, false);
        listView.addFooterView(getItemNormal(this, getString(R.string.list_item_add_unit)), false, true);
    }

    private void reLoad() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        ((TextView) measureNameView.findViewById(R.id.textSecondary)).setText(userMeasure.getName());
    }

    private String getUnitsOrder() {
        StringBuilder order = new StringBuilder("");
        for (ConcreteUnit concreteUnit : concreteMeasure.getConcreteUnits()) {
            order.append(concreteUnit.getName());
            order.append(" ");
        }
        return order.toString();
    }

    private void saveChange() {
        Gson gson = new Gson();
        String concreteFileName = concreteMeasure.getConcreteFileName();
        String userFileName = concreteMeasure.getUserFileName();
        concreteMeasure = userMeasure.getConcreteMeasure(concreteFileName, userFileName);
        try {
            saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
            saveToInternal(this, userFileName, gson.toJson(userMeasure));
            reLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position - COUNT_SETTINGS_ITEMS < 0 || position - COUNT_SETTINGS_ITEMS >= userMeasure.getUnits().size()) {
            switch (position) {
                case 1:
                    View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                    final EditText editText = layout.findViewById(R.id.editText);
                    editText.setText("");
                    editText.append(userMeasure.getName());
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_measure_name)
                            .setView(layout)
                            .setCancelable(true)
                            .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                                userMeasure.setName(editText.getText().toString());
                                Log.i("NAME", "changePos: " + userMeasure.getName());
                                saveChange();
                            }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    }).show();
                    break;
                case 2:
                    Intent intent = new Intent(getApplicationContext(), EditOrderUnitsActivity.class);
                    intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                    startActivity(intent);
                    break;
            }
            return;
        }
        Intent intent = new Intent(getApplicationContext(), EditUnitActivity.class);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unitsAdapter.getItem(position - COUNT_SETTINGS_ITEMS).getUnitName());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_measure, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(), StartActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        startActivity(home);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent home = new Intent(getApplicationContext(), StartActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                startActivity(home);
                finish();
                return true;
            case R.id.menu_delete_converter:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_converter_title)
                        .setMessage(R.string.delete_converter_msg)
                        .setCancelable(true)
                        .setPositiveButton(R.string.delete_converter_yes, (dialog, which) -> {
                            if (getFileStreamPath(concreteMeasure.getConcreteFileName()).delete() &&
                                    getFileStreamPath(concreteMeasure.getUserFileName()).delete()) {
                                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
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
            FileInputStream in = openFileInput(concreteMeasure.getUserFileName());
            Reader reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            String json = gson.toJson(gson.fromJson(reader, Measure.class));

            OutputStream out = getContentResolver().openOutputStream(getFileUri(concreteMeasure.getName()));
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
