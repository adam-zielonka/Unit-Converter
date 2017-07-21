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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import pro.adamzielonka.converter.adapters.ConcreteAdapter;
import pro.adamzielonka.converter.adapters.UnitsAdapter;
import pro.adamzielonka.converter.models.database.CloudMeasure;
import pro.adamzielonka.converter.models.database.User;
import pro.adamzielonka.converter.models.user.Measure;
import pro.adamzielonka.converter.models.user.Unit;
import pro.adamzielonka.converter.services.MyUploadService;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;
import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;
import static pro.adamzielonka.converter.tools.Code.REQUEST_SAVE_TO_DOWNLOAD;
import static pro.adamzielonka.converter.tools.FileTools.getFileUri;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.isExternalStorageWritable;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;
import static pro.adamzielonka.converter.tools.Permissions.getReadAndWritePermissionsStorage;

public class EditMeasureActivity extends EditActivity implements ListView.OnItemClickListener {

    private UnitsAdapter unitsAdapter;

    private View editMeasureNameView;
    private View editUnitOrder;
    private View editDefaultDisplay1;
    private View editDefaultDisplay2;
    private View addUnit;
    private View authorView;
    private View versionView;
    private View cloudView;

    private DatabaseReference mDatabase;
    private static final String TAG = "EditMeasureActivity";
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void onLoad() throws Exception {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgressDialog();
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
        super.onLoad();
        unitsAdapter = new UnitsAdapter(getApplicationContext(), userMeasure.getUnits());
        listView.setAdapter(unitsAdapter);
        listView.setOnItemClickListener(this);

        authorView = listView.addHeaderItem(getString(R.string.list_item_author));
        versionView = listView.addHeaderItem(getString(R.string.list_item_version));
        cloudView = listView.addHeaderItem(getString(R.string.list_item_cloud));
        listView.addHeaderTitle(getString(R.string.list_title_Measure));
        editMeasureNameView = listView.addHeaderItem(getString(R.string.list_item_name));
        editUnitOrder = listView.addHeaderItem(getString(R.string.list_item_units_order));
        editDefaultDisplay1 = listView.addHeaderItem(getString(R.string.list_item_measure_default_1));
        editDefaultDisplay2 = listView.addHeaderItem(getString(R.string.list_item_measure_default_2));
        listView.addHeaderTitle(getString(R.string.list_title_units));
        addUnit = listView.addFooterItem(getString(R.string.list_item_add_unit));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(authorView, userMeasure.getAuthor());
        updateView(versionView, userMeasure.getVersion().toString());
        updateView(cloudView, userMeasure.getCloudID());
        updateView(editMeasureNameView, userMeasure.getName());
        if (userMeasure.getUnits().size() > 0) {
            updateView(editUnitOrder, concreteMeasure.getUnitsOrder());
            updateView(editDefaultDisplay1, concreteMeasure.getConcreteUnits().get(concreteMeasure.getDisplayFrom()).getName());
            updateView(editDefaultDisplay2, concreteMeasure.getConcreteUnits().get(concreteMeasure.getDisplayTo()).getName());
        } else {
            hideView(editUnitOrder);
            hideView(editDefaultDisplay1);
            hideView(editDefaultDisplay2);
        }
        unitsAdapter.clear();
        unitsAdapter.addAll(userMeasure.getUnits());
        unitsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (isUnderItemClick(position, listView.getHeaderViewsCount(), userMeasure.getUnits().size())) {
            unit = unitsAdapter.getItem(position - listView.getHeaderViewsCount());
            startActivityForResult(setEditIntent(EditUnitActivity.class), REQUEST_EDIT_ACTIVITY);
        } else {
            if (view.equals(editMeasureNameView)) {
                EditText editText = getDialogEditText(userMeasure.getName());
                getAlertDialogSave(R.string.dialog_measure_name, editText.getRootView(), (dialog, which) -> {
                    userMeasure.setName(editText.getText().toString());
                    onSave();
                }).show();

            } else if (view.equals(editUnitOrder)) {
                if (userMeasure.getUnits().size() <= 0) return;
                Intent intent = new Intent(getApplicationContext(), EditOrderUnitsActivity.class);
                intent.putExtra(EXTRA_MEASURE_FILE_NAME, concreteMeasure.getConcreteFileName());
                startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);

            } else if (view.equals(editDefaultDisplay1)) {
                if (userMeasure.getUnits().size() <= 0) return;
                ConcreteAdapter concreteAdapter = new ConcreteAdapter(getApplicationContext(),
                        R.layout.layout_spiner_units, concreteMeasure.getConcreteUnits());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_measure_default_1)
                        .setCancelable(true)
                        .setAdapter(concreteAdapter, (dialogInterface, i) -> {
                            userMeasure.setDisplayFrom(i);
                            onSave();
                        }).show();

            } else if (view.equals(editDefaultDisplay2)) {
                if (userMeasure.getUnits().size() <= 0) return;
                ConcreteAdapter concreteAdapter = new ConcreteAdapter(getApplicationContext(),
                        R.layout.layout_spiner_units, concreteMeasure.getConcreteUnits());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_measure_default_2)
                        .setCancelable(true)
                        .setAdapter(concreteAdapter, (dialogInterface, i) -> {
                            userMeasure.setDisplayTo(i);
                            onSave();
                        }).show();

            } else if (view.equals(addUnit)) {
                EditText editText = getDialogEditText("");
                getAlertDialogSave(R.string.dialog_unit_symbol, editText.getRootView(), (dialog, which) -> {
                    String newUnitName = editText.getText().toString();
                    if (!isSymbolUnitExist(newUnitName, userMeasure.getUnits())) {
                        unit = new Unit();
                        unit.setSymbol(newUnitName);
                        userMeasure.getUnits().add(unit);
                        Intent intent = setEditIntent(EditUnitActivity.class);
                        onSave();
                        startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
                    } else {
                        showError(this, R.string.error_symbol_unit_already_exist);
                    }
                }).show();
            }
        }
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
                getAlertDialogDelete(R.string.delete_measure_title, (dialog, which) -> {
                    if (getFileStreamPath(concreteMeasure.getConcreteFileName()).delete() &&
                            getFileStreamPath(concreteMeasure.getUserFileName()).delete()) {
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
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

    private void submitMeasure() {
        String userId = getUid();
        if (userId == null) return;
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(EditMeasureActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            updateMeasure(userId, user.username, userMeasure.getName(), concreteMeasure.getUnitsOrder());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void updateMeasure(String userId, String username, String title, String body) {
        if (userMeasure.getCloudID().equals("")) {
            String key = mDatabase.child("measures").push().getKey();
            CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1);
            doUpdateMeasure(key, cloudMeasure);
        } else {
            Query query = mDatabase.child("measures");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(userMeasure.getCloudID())) {
                        CloudMeasure cloudMeasure = snapshot.child(userMeasure.getCloudID()).getValue(CloudMeasure.class);
                        if (cloudMeasure.uid.equals(getUid())) {
                            cloudMeasure.version++;
                            cloudMeasure.title = title;
                            cloudMeasure.units_symbols = body;
                            cloudMeasure.units_names = body;
                            doUpdateMeasure(userMeasure.getCloudID(), cloudMeasure);
                        } else {
                            String key = mDatabase.child("measures").push().getKey();
                            cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1);
                            doUpdateMeasure(key, cloudMeasure);
                        }
                    } else {
                        String key = mDatabase.child("measures").push().getKey();
                        CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1);
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
        cloudMeasure.file = concreteMeasure.getUserFileName();
        Map<String, Object> postValues = cloudMeasure.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/measures/" + key, postValues);
        childUpdates.put("/user-measures/" + cloudMeasure.uid + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        userMeasure.setCloudID(key);
        onSave(false);
        File file = new File(getFilesDir() + "/" + concreteMeasure.getUserFileName());
        uploadFromUri(Uri.parse(file.toURI().toString()));
    }

    private void uploadFromUri(Uri fileUri) {
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .putExtra(MyUploadService.EXTRA_FILE_USER, getUid())
                .putExtra(MyUploadService.EXTRA_FILE_CONCRETE, concreteMeasure.getConcreteFileName())
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
