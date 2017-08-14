package pro.adamzielonka.converter.activities.edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.StartActivity;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.models.database.CloudMeasure;
import pro.adamzielonka.converter.models.database.User;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.models.file.Prefix;
import pro.adamzielonka.converter.models.file.Unit;
import pro.adamzielonka.converter.services.MyUploadService;
import pro.adamzielonka.converter.tools.Language;
import pro.adamzielonka.itemsview.Item;
import pro.adamzielonka.itemsview.tools.Tests;

import static pro.adamzielonka.converter.tools.Code.REQUEST_SAVE_TO_DOWNLOAD;
import static pro.adamzielonka.converter.tools.FileTools.getFileUri;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.isExternalStorageWritable;
import static pro.adamzielonka.converter.tools.Language.getLangCode;
import static pro.adamzielonka.converter.tools.Language.getLanguageWords;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;
import static pro.adamzielonka.converter.tools.Permissions.getReadAndWritePermissionsStorage;

public class EditMeasureActivity extends EditActivity {

    private DatabaseReference mDatabase;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_measure);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgressDialog();
                if (intent.getAction() != null)
                    switch (intent.getAction()) {
                        case MyUploadService.UPLOAD_COMPLETED:
                            showSuccess(EditMeasureActivity.this, R.string.success_upload);
                            break;
                        case MyUploadService.UPLOAD_ERROR:
                            showError(EditMeasureActivity.this, R.string.msg_error_upload);
                            break;
                    }
            }
        };
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
                .setAction(id -> measure.global = cMeasure.getGlobalFromID((Integer) id))
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_Measure)
                .setTitle(R.string.list_item_name)
                .setUpdate(() -> measure.getName(measure.global))
                .setAction(name -> measure.setName(cMeasure.global, (String) name))
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
                .setAction(id -> measure.displayFrom = (Integer) id)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_measure_default_2)
                .setIf(() -> measure.units.size() > 0)
                .setUpdate(() -> cMeasure.cUnits.get(cMeasure.displayTo).name)
                .setElseUpdate(() -> "")
                .setArray(() -> cMeasure.getUnitsSymbol())
                .setPosition(() -> measure.displayTo)
                .setAction(id -> measure.displayTo = (Integer) id)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_units)
                .setAdapter(adapter)
                .setUpdate(() -> measure.units)
                .setAction(position -> {
                    unit = adapter.getItem((Integer) position);
                    startEditActivity(EditUnitActivity.class);
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_add_unit)
                .setAction(this::addUnit)
                .addValidator(symbol -> Tests.isUnique(symbol, measure.units), getString(R.string.error_symbol_unit_already_exist))
                .addValidator(symbol -> !symbol.equals(""), getString(R.string.error_symbol_empty))
                .add(itemsView);
    }

    private void addUnit(Object symbol) {
        Unit unitTemp = unit = new Unit();
        unit.symbol = (String) symbol;
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
            case R.id.menu_upload_converter:
                submitMeasure();
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
            FileInputStream in = openFileInput(cMeasure.userFileName);
            Reader reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = getGson();
            String json = gson.toJson(gson.fromJson(reader, Measure.class));

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

    private void submitMeasure() {
        String userId = getUid();
        if (userId == null) return;
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Toast.makeText(EditMeasureActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            updateMeasure(userId, user.username, measure.getName(getLangCode(EditMeasureActivity.this)), cMeasure.getUnitsOrder());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateMeasure(String userId, String username, String title, String body) {
        if (measure.cloudID.equals("")) {
            String key = mDatabase.child("measures").push().getKey();
            CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
            doUpdateMeasure(key, cloudMeasure);
        } else {
            Query query = mDatabase.child("measures");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(measure.cloudID)) {
                        CloudMeasure cloudMeasure = snapshot.child(measure.cloudID).getValue(CloudMeasure.class);
                        if (cloudMeasure.uid.equals(getUid())) {
                            cloudMeasure.version++;
                            cloudMeasure.title = title;
                            cloudMeasure.units_symbols = body;
                            cloudMeasure.units_names = body;
                            doUpdateMeasure(measure.cloudID, cloudMeasure);
                        } else {
                            String key = mDatabase.child("measures").push().getKey();
                            cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
                            doUpdateMeasure(key, cloudMeasure);
                        }
                    } else {
                        String key = mDatabase.child("measures").push().getKey();
                        CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
                        doUpdateMeasure(key, cloudMeasure);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    private void doUpdateMeasure(String key, CloudMeasure cloudMeasure) {
        cloudMeasure.file = cMeasure.userFileName;
        Map<String, Object> postValues = cloudMeasure.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/measures/" + key, postValues);
        childUpdates.put("/user-measures/" + cloudMeasure.uid + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        DatabaseReference ref = mDatabase.child("measures").child(key).child("version");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long version = dataSnapshot.getValue(Long.class);
                measure.cloudID = key;
                measure.version = version;
                itemsView.onSave();
                File file = new File(getFilesDir() + "/" + cMeasure.userFileName);
                uploadFromUri(Uri.parse(file.toURI().toString()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void uploadFromUri(Uri fileUri) {
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .putExtra(MyUploadService.EXTRA_FILE_USER, getUid())
                .putExtra(MyUploadService.EXTRA_FILE_CONCRETE, cMeasure.concreteFileName)
                .setAction(MyUploadService.ACTION_UPLOAD));

        showProgressDialog(getString(R.string.progress_uploading));
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

}
