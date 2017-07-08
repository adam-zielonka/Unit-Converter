package pro.adamzielonka.converter.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.FileTools.getFileInternalName;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.openFileToInputStream;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;
import static pro.adamzielonka.converter.tools.Message.showError;

public class AddConverterActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_converter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSampleFiles(@SuppressWarnings("unused") View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bitbucket.org/adam-zielonka-pro/converters/src"));
        startActivity(browserIntent);
    }

    public void onClickFromJsonFile(@SuppressWarnings("unused") View v) {

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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            } else {
                showError(this, R.string.error_no_permissions);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                loadConverter(uri);
            }
        }
    }

    private void loadConverter(Uri uri) {
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
