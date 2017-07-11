package pro.adamzielonka.converter.activities.edit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.StartActivity;
import pro.adamzielonka.converter.adapters.UnitsAdapter;
import pro.adamzielonka.converter.components.MyListView;
import pro.adamzielonka.converter.units.concrete.ConcreteUnit;
import pro.adamzielonka.converter.units.user.Measure;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.FileTools.getFileUri;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.isExternalStorageWritable;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;

public class EditMeasureActivity extends EditActivity implements ListView.OnItemClickListener {

    private UnitsAdapter unitsAdapter;
    private String measureFileName;
    private View measureNameView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int COUNT_SETTINGS_ITEMS = 4;
    private static final int EDIT_NAME = 1;
    private static final int EDIT_ORDER = 2;

    @Override
    public void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");

        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.getUserFileName());
        unitsAdapter = new UnitsAdapter(getApplicationContext(), userMeasure.getUnits());

        MyListView listView = findViewById(R.id.editListView);
        listView.setAdapter(unitsAdapter);
        listView.setOnItemClickListener(this);
        listView.setActivity(this);

        listView.addHeaderTitle(getString(R.string.list_title_Measure));
        measureNameView = listView.addHeaderItem(getString(R.string.list_item_name), userMeasure.getName());
        listView.addHeaderItem(getString(R.string.list_item_units_order), getUnitsOrder());
        listView.addHeaderTitle(getString(R.string.list_title_units));
        listView.addFooterItem(getString(R.string.list_item_add_unit));
    }

    @Override
    public void onReload() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(measureFileName);
        userMeasure = openMeasure(concreteMeasure.getUserFileName());
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position - COUNT_SETTINGS_ITEMS < 0 || position - COUNT_SETTINGS_ITEMS >= userMeasure.getUnits().size()) {
            switch (position) {
                case EDIT_NAME:
                    View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                    final EditText editText = layout.findViewById(R.id.editText);
                    editText.setText(userMeasure.getName());
                    editText.setSelection(editText.length());
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_measure_name)
                            .setView(layout)
                            .setCancelable(true)
                            .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                                userMeasure.setName(editText.getText().toString());
                                onSave();
                            }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    }).show();
                    break;
                case EDIT_ORDER:
                    Intent intent = new Intent(getApplicationContext(), EditOrderUnitsActivity.class);
                    intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                    startActivity(intent);
                    break;
                default:
                    Unit newUnit = new Unit();
                    String newUnitName = "";
                    for (int i = 1; isSymbolUnitExist(newUnitName, userMeasure.getUnits()); i++) {
                        newUnitName = "" + i;
                    }
                    newUnit.setSymbol(newUnitName);
                    userMeasure.getUnits().add(newUnit);
                    onSave(false);
                    Intent addUnit = new Intent(getApplicationContext(), EditUnitActivity.class);
                    addUnit.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                    addUnit.putExtra("unitName", newUnitName);
                    startActivity(addUnit);
            }
            return;
        }
        Unit unit = unitsAdapter.getItem(position - COUNT_SETTINGS_ITEMS);
        Intent intent = new Intent(getApplicationContext(), EditUnitActivity.class);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit != null ? unit.getSymbol() : "");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_measure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_delete_converter:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_measure_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                            if (getFileStreamPath(concreteMeasure.getConcreteFileName()).delete() &&
                                    getFileStreamPath(concreteMeasure.getUserFileName()).delete()) {
                                onBackPressed();
                            }
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                return true;
            case R.id.menu_save_converter:
                saveToDownloadsStep1();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //region save to downloads
    private void saveToDownloadsStep1() {
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
                saveToDownloadsStep2();
            } else {
                showError(this, R.string.error_no_permissions_to_write_file);
            }
        }
    }

    private void saveToDownloadsStep2() {
        try {
            FileInputStream in = openFileInput(concreteMeasure.getUserFileName());
            Reader reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = getGson();
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
    //endregion

}
