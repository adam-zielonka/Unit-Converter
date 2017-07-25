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
import static pro.adamzielonka.converter.tools.Language.getLangCode;
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
    private View langView;
    private View globalLangView;

    private DatabaseReference mDatabase;
    private static final String TAG = "EditMeasureActivity";
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_edit_measure);
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
        unitsAdapter = new UnitsAdapter(getApplicationContext(), userMeasure.units, userMeasure.global, userMeasure.global);
        listView.setAdapter(unitsAdapter);
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_language));
        langView = listView.addHeaderItem(getString(R.string.list_item_language_available));
        globalLangView = listView.addHeaderItem(getString(R.string.list_item_language_global));
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
        updateView(langView, concreteMeasure.languages.toString());
        updateView(globalLangView, concreteMeasure.global);
        updateView(editMeasureNameView, userMeasure.getName(userMeasure.global));
        if (userMeasure.units.size() > 0) {
            updateView(editUnitOrder, concreteMeasure.getUnitsOrder());
            updateView(editDefaultDisplay1, concreteMeasure.concreteUnits.get(concreteMeasure.displayFrom).name);
            updateView(editDefaultDisplay2, concreteMeasure.concreteUnits.get(concreteMeasure.displayTo).name);
        } else {
            hideView(editUnitOrder);
            hideView(editDefaultDisplay1);
            hideView(editDefaultDisplay2);
        }
        unitsAdapter.clear();
        unitsAdapter.langCode = unitsAdapter.globalCode = userMeasure.global;
        unitsAdapter.addAll(userMeasure.units);
        unitsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (isUnderItemClick(position, listView.getHeaderViewsCount(), userMeasure.units.size())) {
            unit = unitsAdapter.getItem(position - listView.getHeaderViewsCount());
            startActivityForResult(setEditIntent(EditUnitActivity.class), REQUEST_EDIT_ACTIVITY);
        } else {
            if (view.equals(globalLangView)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.list_item_language_global)
                        .setSingleChoiceItems(concreteMeasure.getGlobalLangs(), concreteMeasure.getGlobalID(), (dialogInterface, i) -> {
                            int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                            userMeasure.global = concreteMeasure.getGlobalFromID(selectedPosition);
                            dialogInterface.dismiss();
                            onSave();
                        })
                        .setCancelable(true)
                        .show();

            } else if (view.equals(editMeasureNameView)) {
                EditText editText = getDialogEditText(userMeasure.getName(userMeasure.global));
                getAlertDialogSave(R.string.dialog_measure_name, editText.getRootView(), (dialog, which) -> {
                    userMeasure.setName(concreteMeasure.global, editText.getText().toString());
                    onSave();
                }).show();

            } else if (view.equals(editUnitOrder) && editUnitOrder.isEnabled()) {
                Intent intent = new Intent(getApplicationContext(), EditOrderUnitsActivity.class);
                intent.putExtra(EXTRA_MEASURE_FILE_NAME, concreteMeasure.concreteFileName);
                startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);

            } else if (view.equals(editDefaultDisplay1) && editDefaultDisplay1.isEnabled()) {
                ConcreteAdapter concreteAdapter = new ConcreteAdapter(getApplicationContext(),
                        R.layout.spiner_units, concreteMeasure.concreteUnits,
                        userMeasure.global,
                        userMeasure.global
                );
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_measure_default_1)
                        .setCancelable(true)
                        .setAdapter(concreteAdapter, (dialogInterface, i) -> {
                            userMeasure.displayFrom = i;
                            onSave();
                        }).show();

            } else if (view.equals(editDefaultDisplay2) && editDefaultDisplay2.isEnabled()) {
                ConcreteAdapter concreteAdapter = new ConcreteAdapter(getApplicationContext(),
                        R.layout.spiner_units, concreteMeasure.concreteUnits,
                        userMeasure.global,
                        userMeasure.global
                );
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_measure_default_2)
                        .setCancelable(true)
                        .setAdapter(concreteAdapter, (dialogInterface, i) -> {
                            userMeasure.displayTo = i;
                            onSave();
                        }).show();

            } else if (view.equals(addUnit)) {
                EditText editText = getDialogEditText("");
                getAlertDialogSave(R.string.dialog_unit_symbol, editText.getRootView(), (dialog, which) -> {
                    String newUnitName = editText.getText().toString();
                    if (!isSymbolUnitExist(newUnitName, userMeasure.units)) {
                        unit = new Unit();
                        unit.symbol = newUnitName;
                        userMeasure.units.add(unit);
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
                    if (getFileStreamPath(concreteMeasure.concreteFileName).delete() &&
                            getFileStreamPath(concreteMeasure.userFileName).delete()) {
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
            FileInputStream in = openFileInput(concreteMeasure.userFileName);
            Reader reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = getGson();
            String json = gson.toJson(gson.fromJson(reader, Measure.class));

            OutputStream out = getContentResolver().openOutputStream(getFileUri(concreteMeasure.getName(userMeasure.global)));
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
                            updateMeasure(userId, user.username, userMeasure.getName(getLangCode(EditMeasureActivity.this)), concreteMeasure.getUnitsOrder());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void updateMeasure(String userId, String username, String title, String body) {
        if (userMeasure.cloudID.equals("")) {
            String key = mDatabase.child("measures").push().getKey();
            CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
            doUpdateMeasure(key, cloudMeasure);
        } else {
            Query query = mDatabase.child("measures");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(userMeasure.cloudID)) {
                        CloudMeasure cloudMeasure = snapshot.child(userMeasure.cloudID).getValue(CloudMeasure.class);
                        if (cloudMeasure.uid.equals(getUid())) {
                            cloudMeasure.version++;
                            cloudMeasure.title = title;
                            cloudMeasure.units_symbols = body;
                            cloudMeasure.units_names = body;
                            doUpdateMeasure(userMeasure.cloudID, cloudMeasure);
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
        cloudMeasure.file = concreteMeasure.userFileName;
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
                userMeasure.cloudID = key;
                userMeasure.version = version;
                onSave(false);
                File file = new File(getFilesDir() + "/" + concreteMeasure.userFileName);
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
                .putExtra(MyUploadService.EXTRA_FILE_CONCRETE, concreteMeasure.concreteFileName)
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
