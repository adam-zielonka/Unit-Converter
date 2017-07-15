package pro.adamzielonka.converter.activities.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.StartActivity;
import pro.adamzielonka.converter.activities.cloud.CloudListActivity;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.Code.REQUEST_ADD_FROM_FILE;
import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Code.RESULT_ADD_FROM_FILE;
import static pro.adamzielonka.converter.tools.FileTools.getFileInternalName;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.openFileToInputStream;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Permissions.getReadAndWritePermissionsStorage;

public class AddMeasureActivity extends EditActivity implements ListView.OnItemClickListener {

    private View addByCreateView;
    private View addFromFileView;
    private View getFileView;
    private View addFromCloudView;

    @Override
    public void onLoad() throws FileNotFoundException {
        listView = findViewById(R.id.editListView);
        listView.setActivity(this);
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_add_measure));
        addByCreateView = listView.addHeaderItem(getString(R.string.list_item_create), getString(R.string.list_item_create_description));
        addFromFileView = listView.addHeaderItem(getString(R.string.list_item_load_from_json), getString(R.string.list_item_load_from_json_description));
        getFileView = listView.addHeaderItem(getString(R.string.list_item_json_repo), getString(R.string.list_item_json_repo_description));
        addFromCloudView = listView.addHeaderItem(getString(R.string.list_item_load_form_cloud), getString(R.string.list_item_load_form_cloud_description), false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(addByCreateView)) {
            View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            EditText editText = getDialogEditText(layout, "");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_measure_name)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        userMeasure = new Measure();
                        userMeasure.setName(editText.getText().toString());
                        concreteMeasure = userMeasure.getConcreteMeasure();

                        String concreteFileName = getFileInternalName(this,
                                "concrete_", concreteMeasure.getName());
                        String userFileName = getFileInternalName(this,
                                "user_", concreteMeasure.getName());

                        concreteMeasure.setConcreteFileName(concreteFileName);
                        concreteMeasure.setUserFileName(userFileName);
                        onSave(false);
                        Intent intent = new Intent(getApplicationContext(), EditMeasureActivity.class);
                        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
                        startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(addFromFileView)) {
            ActivityCompat.requestPermissions(this,
                    getReadAndWritePermissionsStorage(), REQUEST_ADD_FROM_FILE);

        } else if (view.equals(getFileView)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://bitbucket.org/adam-zielonka-pro/converters/src"));
            startActivity(browserIntent);

        } else if (view.equals(addFromCloudView)) {
            Intent intent = new Intent(getApplicationContext(), CloudListActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Log.i("RESULT", "onActivityResult: " + requestCode + " " + resultCode);
        if (requestCode == RESULT_ADD_FROM_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                addConverterFromFile(uri);
            }
        } else if (requestCode == REQUEST_EDIT_ACTIVITY) {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
            startActivity(intent);
            finish();
        }
    }

    private void addConverterFromFile(Uri uri) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileToInputStream(this, uri)));

            Gson gson = getGson();
            Measure userMeasure = gson.fromJson(reader, Measure.class);
            ConcreteMeasure concreteMeasure = userMeasure.getConcreteMeasure();

            if (!concreteMeasure.isCorrect()) {
                showError(this, R.string.error_no_units);
                return;
            }

            String concreteFileName = getFileInternalName(this, "concrete_", concreteMeasure.getName());
            String userFileName = getFileInternalName(this, "user_", concreteMeasure.getName());

            concreteMeasure.setConcreteFileName(concreteFileName);
            concreteMeasure.setUserFileName(userFileName);

            saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
            saveToInternal(this, userFileName, gson.toJson(userMeasure));

            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
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
