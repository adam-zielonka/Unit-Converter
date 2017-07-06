package pro.adamzielonka.converter.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.UnitsAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.FileTools.getFileUri;
import static pro.adamzielonka.converter.tools.FileTools.isExternalStorageWritable;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;

public class EditMeasureActivity extends AppCompatActivity {
    Measure userMeasure;
    ConcreteMeasure concreteMeasure;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

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
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        Intent intent = getIntent();
        String measureFileName = intent.getStringExtra("measureFileName");
        try {
            concreteMeasure = openConcreteMeasure(measureFileName);
            userMeasure = openMeasure(concreteMeasure.getUserFileName());
            UnitsAdapter unitsAdapter = new UnitsAdapter(getApplicationContext(), userMeasure.getUnits());
            ListView listView = findViewById(R.id.unitsList);
            listView.setAdapter(unitsAdapter);
            Button editText = findViewById(R.id.editMeasureName);
            editText.setText(userMeasure.getName());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_measure, menu);
        return true;
    }

    private ConcreteMeasure openConcreteMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        return gson.fromJson(reader, ConcreteMeasure.class);
    }

    private Measure openMeasure(String fileName) throws FileNotFoundException {
        FileInputStream in = openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        return gson.fromJson(reader, Measure.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
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
