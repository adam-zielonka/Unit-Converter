package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.OutputStream;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.StartActivity;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.models.file.Prefix;
import pro.adamzielonka.converter.models.file.Unit;
import pro.adamzielonka.converter.tools.Language;
import pro.adamzielonka.items.Item;
import pro.adamzielonka.verification.Tests;

import static pro.adamzielonka.converter.names.Code.REQUEST_SAVE_TO_DOWNLOAD;
import static pro.adamzielonka.converter.tools.Language.getLanguageWords;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;
import static pro.adamzielonka.converter.tools.Permissions.getReadAndWritePermissionsStorage;
import static pro.adamzielonka.file.FileTools.toJSON;
import static pro.adamzielonka.file.Open.openJSON;
import static pro.adamzielonka.file.Save.isExternalStorageWritable;

public class EditMeasureActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_measure);
        super.addItems();
        ArrayAdapter<Unit> adapter = new MyArrayAdapter<Unit>(getApplicationContext(), measure.units) {
            @Override
            public void setView(Unit item, TextView textPrimary, TextView textSecondary) {
                String description = getLanguageWords(item.descriptionPrefix, measure.global)
                        + getLanguageWords(item.description, measure.global);
                String unitName = item.symbol + (!description.isEmpty() ? " - " + description : "");

                StringBuilder prefixes = new StringBuilder("");
                for (Prefix prefix : item.prefixes) {
                    prefixes.append(prefix.symbol).append(item.symbol);
                    prefixes.append(" ");
                }
                textPrimary.setText(unitName);
                textSecondary.setText(prefixes.toString());
            }
        };

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_language)
                .setTitle(R.string.list_item_language_available)
                .setUpdate(() -> cMeasure.languages.toString())
                .setAction(() -> startEditActivity(EditLanguagesActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_language_global)
                .setUpdate(() -> Language.getLanguage(cMeasure.global))
                .setArray(() -> cMeasure.getGlobalLangs())
                .setPosition(() -> cMeasure.getGlobalID())
                .setAction((Integer id) -> measure.global = cMeasure.getGlobalFromID(id))
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_Measure)
                .setTitle(R.string.list_item_name)
                .setUpdate(() -> measure.getName(measure.global))
                .setAction((String name) -> measure.setName(cMeasure.global, name))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_units_order)
                .setIf(() -> measure.units.size() > 0)
                .setUpdate(() -> cMeasure.getUnitsOrder())
                .setAction(() -> startEditActivity(EditOrderUnitsActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_measure_default_1)
                .setIf(() -> measure.units.size() > 0)
                .setUpdate(() -> cMeasure.cUnits.get(cMeasure.displayFrom).name)
                .setElseUpdate(() -> "")
                .setArray(() -> cMeasure.getUnitsSymbol())
                .setPosition(() -> measure.displayFrom)
                .setAction((Integer id) -> measure.displayFrom = id)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_measure_default_2)
                .setIf(() -> measure.units.size() > 0)
                .setUpdate(() -> cMeasure.cUnits.get(cMeasure.displayTo).name)
                .setElseUpdate(() -> "")
                .setArray(() -> cMeasure.getUnitsSymbol())
                .setPosition(() -> measure.displayTo)
                .setAction((Integer id) -> measure.displayTo = id)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_units)
                .setAdapter(adapter)
                .setUpdateAdapter(() -> measure.units)
                .setActionAdapter((Integer position) -> {
                    unit = adapter.getItem(position);
                    startEditActivity(EditUnitActivity.class);
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_add_unit)
                .setAction(this::addUnit)
                .addValidator(symbol -> Tests.isUnique(symbol, measure.units), getString(R.string.error_symbol_unit_already_exist))
                .addValidator(symbol -> !symbol.equals(""), getString(R.string.error_symbol_empty))
                .add(itemsView);
    }

    private void addUnit(String symbol) {
        Unit unitTemp = unit = new Unit();
        unit.symbol = symbol;
        measure.units.add(unit);
        itemsView.onSave();
        unit = unitTemp;
        startEditActivity(EditUnitActivity.class);
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
            case R.id.menu_delete_converter:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_measure_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_delete, (d, i) -> {
                            if (getFileStreamPath(cMeasure.concreteFileName).delete() &&
                                    getFileStreamPath(cMeasure.userFileName).delete()) {
                                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                return true;
            case R.id.menu_save_converter:
                ActivityCompat.requestPermissions(this,
                        getReadAndWritePermissionsStorage(), REQUEST_SAVE_TO_DOWNLOAD);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SAVE_TO_DOWNLOAD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && isExternalStorageWritable()) {
                saveToDownloads();
            } else {
                showError(this, R.string.error_no_permissions_to_write_file);
            }
        }
    }

    private void saveToDownloads() {
        try {
            String json = toJSON(openJSON(this, cMeasure.userFileName, Measure.class));

            OutputStream out = getContentResolver().openOutputStream(getFileUri(cMeasure.getName(measure.global)));
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
