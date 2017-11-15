package pro.adamzielonka.converter.activities.edit;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;

import java.io.OutputStream;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.StartActivity;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.models.concrete.CUnit;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.settings.Language;
import pro.adamzielonka.items.Item;

import static pro.adamzielonka.converter.names.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.names.Code.REQUEST_SAVE_TO_DOWNLOAD;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;
import static pro.adamzielonka.converter.tools.Permissions.getReadAndWritePermissionsStorage;
import static pro.adamzielonka.file.FileTools.toJSON;
import static pro.adamzielonka.file.Open.openJSON;
import static pro.adamzielonka.file.Save.isExternalStorageWritable;

public class DetailMeasureActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_detail_measure);
        super.addItems();

        new Item.Builder(this)
                .setTitleHeader(R.string.measure_details)
                .setTitle(R.string.list_item_name)
                .setUpdate(() -> measure.getName(cMeasure.getOwnLang(this)))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_author)
                .setUpdate(this::getAuthors)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_units)
                .setUpdate(this::getMeasures)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_languages)
                .setUpdate(this::getLanguages)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.local_settings)
                .setTitle(R.string.list_own_name_measure)
                .setSwitcherUpdate(() -> cMeasure.isOwnName)
                .setSwitcherAction(isOwnName -> cMeasure.isOwnName = isOwnName)
                .setUpdate(() -> cMeasure.ownName)
                .setAction((String ownName) -> cMeasure.ownName = ownName)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_own_lang_measure)
                .setSwitcherUpdate(() -> cMeasure.isOwnLang)
                .setSwitcherAction(isOwnLang -> cMeasure.isOwnLang = isOwnLang)
                .setUpdate(() -> Language.getLanguage(cMeasure.ownLang))
                .setArray(() -> cMeasure.getGlobalLangs())
                .setPosition(() -> cMeasure.getOwnLangID())
                .setAction((Integer position) -> cMeasure.ownLang = cMeasure.getGlobalFromID(position))
                .add(itemsView);
    }

    private String getAuthors() {
        StringBuilder builder = new StringBuilder();

        for (String author : measure.author) {
            if (builder.length() != 0) builder.append("\n");
            builder.append(author);
        }

        return builder.toString();
    }

    private String getMeasures() {
        StringBuilder builder = new StringBuilder();

        for (CUnit cUnit : cMeasure.cUnits) {
            if (builder.length() != 0) builder.append("\n");
            builder.append(cUnit.name).append(" - ").append(cUnit.description.get(cMeasure.getOwnLang(this), cMeasure.global));
        }

        return builder.toString();
    }

    private String getLanguages() {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Integer> entry : cMeasure.languages.entrySet()) {
            if (builder.length() != 0) builder.append("\n");
            builder.append(entry.getKey()).append(" - ").append(Language.getLanguage(entry.getKey(), cMeasure.getOwnLang(this)));
        }

        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_measure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_edit_converter:
                startActivityForResult(setEditIntent(EditMeasureActivity.class), REQUEST_EDIT_ACTIVITY);
                return true;
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
