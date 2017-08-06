package pro.adamzielonka.converter.activities.edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;
import pro.adamzielonka.converter.services.MyUploadService;
import pro.adamzielonka.items.classes.Item;
import pro.adamzielonka.items.tools.Tests;

import static pro.adamzielonka.converter.tools.Language.getLanguageWords;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Message.showSuccess;

public class EditMeasureActivity extends EditActivity {

    private DatabaseReference mDatabase;
    private static final String TAG = "EditMeasureActivity";
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
        ArrayAdapter<Unit> adapter = new MyArrayAdapter<Unit>(getApplicationContext(), userMeasure.units) {
            @Override
            public void setView(Unit item, TextView textPrimary, TextView textSecondary) {
                String description = getLanguageWords(item.descriptionPrefix, userMeasure.global)
                        + getLanguageWords(item.description, userMeasure.global);
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
                .setUpdate(() -> concreteMeasure.languages.toString())
                .setAction(() -> startEditActivity(EditLanguagesActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_language_global)
                .setUpdate(() -> concreteMeasure.global)
                .setArray(() -> concreteMeasure.getGlobalLangs())
                .setPosition(() -> concreteMeasure.getGlobalID())
                .setAction(id -> userMeasure.global = concreteMeasure.getGlobalFromID((Integer) id))
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_Measure)
                .setTitle(R.string.list_item_name)
                .setUpdate(() -> userMeasure.getName(userMeasure.global))
                .setAction(name -> userMeasure.setName(concreteMeasure.global, (String) name))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_units_order)
                .setIf(() -> userMeasure.units.size() > 0)
                .setUpdate(() -> concreteMeasure.getUnitsOrder())
                .setAction(() -> startEditActivity(EditOrderUnitsActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_measure_default_1)
                .setIf(() -> userMeasure.units.size() > 0)
                .setUpdate(() -> concreteMeasure.concreteUnits.get(concreteMeasure.displayFrom).name)
                .setElseUpdate(() -> "")
                .setArray(() -> concreteMeasure.getUnitsSymbol())
                .setPosition(() -> userMeasure.displayFrom)
                .setAction(id -> userMeasure.displayFrom = (Integer) id)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_measure_default_2)
                .setIf(() -> userMeasure.units.size() > 0)
                .setUpdate(() -> concreteMeasure.concreteUnits.get(concreteMeasure.displayTo).name)
                .setElseUpdate(() -> "")
                .setArray(() -> concreteMeasure.getUnitsSymbol())
                .setPosition(() -> userMeasure.displayTo)
                .setAction(id -> userMeasure.displayTo = (Integer) id)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_units)
                .setAdapter(adapter)
                .setUpdate(() -> userMeasure.units)
                .setAction(position -> {
                    unit = adapter.getItem((Integer) position);
                    startEditActivity(EditUnitActivity.class);
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_add_unit)
                .setAction(this::addUnit)
                .setValidator(symbol -> Tests.isUnique(symbol, userMeasure.units), getString(R.string.error_symbol_unit_already_exist))
                .add(itemsView);
    }

    private void addUnit(Object symbol) {
        unit = new Unit();
        unit.symbol = (String) symbol;
        userMeasure.units.add(unit);
        itemsView.onSave();
        itemsView.onUpdate();
        unitName = (String) symbol;
        startEditActivity(EditUnitActivity.class);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_edit_measure, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.menu_delete_converter:
//                newAlertDialogDelete(R.string.delete_measure_title, () -> {
//                    if (getFileStreamPath(concreteMeasure.concreteFileName).delete() &&
//                            getFileStreamPath(concreteMeasure.userFileName).delete()) {
//                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//                return true;
//            case R.id.menu_save_converter:
//                ActivityCompat.requestPermissions(this,
//                        getReadAndWritePermissionsStorage(), REQUEST_SAVE_TO_DOWNLOAD);
//                return true;
//            case R.id.menu_upload_converter:
//                submitMeasure();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_SAVE_TO_DOWNLOAD) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && isExternalStorageWritable()) {
//                saveToDownloads();
//            } else {
//                showError(this, R.string.error_no_permissions_to_write_file);
//            }
//        }
//    }
//
//    private void saveToDownloads() {
//        try {
//            FileInputStream in = openFileInput(concreteMeasure.userFileName);
//            Reader reader = new BufferedReader(new InputStreamReader(in));
//            Gson gson = getGson();
//            String json = gson.toJson(gson.fromJson(reader, Measure.class));
//
//            OutputStream out = getContentResolver().openOutputStream(getFileUri(concreteMeasure.getName(userMeasure.global)));
//            if (out != null) {
//                out.write(json.getBytes());
//                out.close();
//                showSuccess(this, R.string.success_save_to_downloads);
//            } else showError(this, R.string.error_create_file);
//        } catch (Exception e) {
//            e.printStackTrace();
//            showError(this, R.string.error_create_file);
//        }
//    }
//
//    private void submitMeasure() {
//        String userId = getUid();
//        if (userId == null) return;
//        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//
//                        if (user == null) {
//                            Log.e(TAG, "User " + userId + " is unexpectedly null");
//                            Toast.makeText(EditMeasureActivity.this,
//                                    "Error: could not fetch user.",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            updateMeasure(userId, user.username, userMeasure.getName(getLangCode(EditMeasureActivity.this)), concreteMeasure.getUnitsOrder());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                    }
//                });
//    }
//
//    private void updateMeasure(String userId, String username, String title, String body) {
//        if (userMeasure.cloudID.equals("")) {
//            String key = mDatabase.child("measures").push().getKey();
//            CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
//            doUpdateMeasure(key, cloudMeasure);
//        } else {
//            Query query = mDatabase.child("measures");
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    if (snapshot.hasChild(userMeasure.cloudID)) {
//                        CloudMeasure cloudMeasure = snapshot.child(userMeasure.cloudID).getValue(CloudMeasure.class);
//                        if (cloudMeasure.uid.equals(getUid())) {
//                            cloudMeasure.version++;
//                            cloudMeasure.title = title;
//                            cloudMeasure.units_symbols = body;
//                            cloudMeasure.units_names = body;
//                            doUpdateMeasure(userMeasure.cloudID, cloudMeasure);
//                        } else {
//                            String key = mDatabase.child("measures").push().getKey();
//                            cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
//                            doUpdateMeasure(key, cloudMeasure);
//                        }
//                    } else {
//                        String key = mDatabase.child("measures").push().getKey();
//                        CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
//                        doUpdateMeasure(key, cloudMeasure);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//
//    }
//
//    private void doUpdateMeasure(String key, CloudMeasure cloudMeasure) {
//        cloudMeasure.file = concreteMeasure.userFileName;
//        Map<String, Object> postValues = cloudMeasure.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/measures/" + key, postValues);
//        childUpdates.put("/user-measures/" + cloudMeasure.uid + "/" + key, postValues);
//
//        mDatabase.updateChildren(childUpdates);
//        DatabaseReference ref = mDatabase.child("measures").child(key).child("version");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Long version = dataSnapshot.getValue(Long.class);
//                userMeasure.cloudID = key;
//                userMeasure.version = version;
//                onSave(false);
//                File file = new File(getFilesDir() + "/" + concreteMeasure.userFileName);
//                uploadFromUri(Uri.parse(file.toURI().toString()));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void uploadFromUri(Uri fileUri) {
//        startService(new Intent(this, MyUploadService.class)
//                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
//                .putExtra(MyUploadService.EXTRA_FILE_USER, getUid())
//                .putExtra(MyUploadService.EXTRA_FILE_CONCRETE, concreteMeasure.concreteFileName)
//                .setAction(MyUploadService.ACTION_UPLOAD));
//
//        showProgressDialog(getString(R.string.progress_uploading));
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
//        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
//    }

}
