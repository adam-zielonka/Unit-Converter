package pro.adamzielonka.converter.activities.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.IOException;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.StartActivity;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.names.Extra;
import pro.adamzielonka.items.Item;

import static pro.adamzielonka.converter.names.Code.REQUEST_ADD_FROM_FILE;
import static pro.adamzielonka.converter.names.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.names.Code.RESULT_ADD_FROM_FILE;
import static pro.adamzielonka.converter.tools.Language.getLangCode;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Permissions.getReadAndWritePermissionsStorage;
import static pro.adamzielonka.file.Open.openJSON;
import static pro.adamzielonka.file.Save.getNewFileInternalName;
import static pro.adamzielonka.file.Save.saveJSON;

public class AddMeasureActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_add_measure);
        super.addItems();

        new Item.Builder(this)
                .setTitleHeader(R.string.list_add_measure)
                .setTitle(R.string.list_item_create_description)
                .setAction(this::createByEditor)
                .addValidator(symbol -> !symbol.equals(""), getString(R.string.error_symbol_empty))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_load_from_json)
                .setUpdate(() -> getString(R.string.list_item_load_from_json_description))
                .setAction(() -> ActivityCompat.requestPermissions(this,
                        getReadAndWritePermissionsStorage(), REQUEST_ADD_FROM_FILE))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_json_repo)
                .setUpdate(() -> getString(R.string.list_item_json_repo_description))
                .setAction(() -> startWebsite(R.string.uri_measures))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_load_form_cloud)
                .setUpdate(() -> getString(R.string.list_item_load_form_cloud_description))
                .add(itemsView);

    }

    private void createByEditor(String name) {
        measure = new Measure();
        measure.setName(getLangCode(this), name);
        measure.global = getLangCode(this);
        cMeasure = measure.getConcreteMeasure();

        String concreteFileName = getNewFileInternalName(this,
                "concrete_", cMeasure.getName(getLangCode(this)));
        String userFileName = getNewFileInternalName(this,
                "user_", cMeasure.getName(getLangCode(this)));

        cMeasure.concreteFileName = concreteFileName;
        cMeasure.userFileName = userFileName;
        try {
            saveMeasure();
            setResultCode(RESULT_OK);
            startEditActivity(EditMeasureActivity.class);
        } catch (Exception e) {
            showError(this, R.string.error_could_not_save_changes);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ADD_FROM_FILE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, RESULT_ADD_FROM_FILE);
            } else {
                showError(this, R.string.error_no_permissions);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_ADD_FROM_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                addConverterFromFile(uri);
            }
        } else if (requestCode == REQUEST_EDIT_ACTIVITY) {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish();
    }

    private void addConverterFromFile(Uri uri) {
        try {
            Measure measure = openJSON(this, uri, Measure.class);
            CMeasure cMeasure = measure.getConcreteMeasure();

            if (!cMeasure.isCorrect()) {
                showError(this, R.string.error_no_units);
                return;
            }

            String concreteFileName = getNewFileInternalName(this, "concrete_", cMeasure.getName(getLangCode(this)));
            String userFileName = getNewFileInternalName(this, "user_", cMeasure.getName(getLangCode(this)));

            cMeasure.concreteFileName = concreteFileName;
            cMeasure.userFileName = userFileName;

            saveJSON(this, concreteFileName, cMeasure);
            saveJSON(this, userFileName, measure);

            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.putExtra(Extra.MEASURE_FILE_NAME, cMeasure.concreteFileName);
            startActivity(intent);
            finish();
        } catch (FileNotFoundException e) {
            showError(this, R.string.error_no_file);
        } catch (IOException e) {
            showError(this, R.string.error_no_json_file);
        } catch (Exception e) {
            showError(this, R.string.error_no_json_file);
        }
    }
}
