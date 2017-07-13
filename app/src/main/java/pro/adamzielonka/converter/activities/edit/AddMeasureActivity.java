package pro.adamzielonka.converter.activities.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import static pro.adamzielonka.converter.tools.FileTools.getFileInternalName;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.openFileToInputStream;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Permissions.getReadAndWritePermissionsStorage;

public class AddMeasureActivity extends EditActivity implements ListView.OnItemClickListener {

    private static final int RESULT_ADD_FROM_FILE = 42;
    private static final int REQUEST_ADD_FROM_FILE = 1;

    private View addByCreateView;
    private View addFromFileView;
    private View getFileView;
    private View addFromCloudView;

    @Override
    public void onLoad() throws FileNotFoundException {
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);
        listView.setActivity(this);

        listView.addHeaderTitle(getString(R.string.list_add_measure));
        addByCreateView = listView.addHeaderItem(getString(R.string.list_item_create), getString(R.string.list_item_create_description));
        addFromFileView = listView.addHeaderItem(getString(R.string.list_item_load_from_json), getString(R.string.list_item_load_from_json_description));
        getFileView = listView.addHeaderItem(getString(R.string.list_item_json_repo), getString(R.string.list_item_json_repo_description));
        addFromCloudView = listView.addHeaderItem(getString(R.string.list_item_load_form_cloud), getString(R.string.list_item_load_form_cloud_description));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(addByCreateView)) {
            userMeasure = new Measure();
            userMeasure.setName("Measure");
            concreteMeasure = userMeasure.getConcreteMeasure();

            String concreteFileName = getFileInternalName(this, "concrete_", concreteMeasure.getName());
            String userFileName = getFileInternalName(this, "user_", concreteMeasure.getName());

            concreteMeasure.setConcreteFileName(concreteFileName);
            concreteMeasure.setUserFileName(userFileName);
            onSave(false);
            Intent addIntent = new Intent(getApplicationContext(), EditMeasureActivity.class);
            addIntent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
            startActivity(addIntent);

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
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
        if (requestCode == RESULT_ADD_FROM_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                addConverterFromFile(uri);
            }
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
