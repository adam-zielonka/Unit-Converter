package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;
import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;

public class SetMeasureActivity extends EditActivity implements ListView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    private View authorView;
    private View versionView;
    private View ownNameSwitchView;
    private View ownNameView;
    private View ownLangSwitchView;
    private View ownLangView;

    private DatabaseReference mDatabase;

    private Long version;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_set_measure);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnCheckedChangeListener(this);
        listView.setOnItemClickListener(this);

        authorView = listView.addItem(getString(R.string.list_item_author), userMeasure.author);
        versionView = listView.addItem(getString(R.string.list_item_version));
        listView.addItemTitle(getString(R.string.local_settings));
        ownNameSwitchView = listView.addItemSwitch(getString(R.string.list_own_name_measure), "");
        ownNameView = listView.addItem(getString(R.string.list_item_name));
        ownLangSwitchView = listView.addItemSwitch(getString(R.string.list_own_lang_measure), "");
        ownLangView = listView.addItem(getString(R.string.list_title_language));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(authorView, userMeasure.author, false);
        if (!userMeasure.cloudID.equals("")) {
            updateView(versionView, userMeasure.version.toString());
            checkVersion();
        } else {
            updateView(versionView, getString(R.string.local_measure));
        }
        setSwitchState(ownNameSwitchView, concreteMeasure.isOwnName);
        updateView(ownNameView, concreteMeasure.ownName, concreteMeasure.isOwnName);
        setSwitchState(ownLangSwitchView, concreteMeasure.isOwnLang);
        updateView(ownLangView, concreteMeasure.ownLang, concreteMeasure.isOwnLang);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (isUserCheckedChanged) {
            concreteMeasure.isOwnName = getSwitchState(ownNameSwitchView);
            concreteMeasure.isOwnLang = getSwitchState(ownLangSwitchView);
            onSave();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (!view.isEnabled()) return;

        if (view.equals(versionView)) {
            if (version != null) {
                if (version > userMeasure.version) {
                    //download update
                }
            } else {
                if (!userMeasure.cloudID.equals("")) {
                    version = userMeasure.version;
                    checkVersion();
                }
            }

        } else if (view.equals(ownNameView)) {
            EditText editText = getDialogEditText(concreteMeasure.ownName);
            getAlertDialogSave(R.string.list_item_name, editText.getRootView(), (d, i) -> {
                concreteMeasure.ownName = editText.getText().toString();
                onSave();
            }).show();

        } else if (view.equals(ownLangView)) {
            getAlertDialog(R.string.list_title_language)
                    .setSingleChoiceItems(concreteMeasure.getGlobalLangs(), concreteMeasure.getOwnLangID(), (dialogInterface, i) -> {
                        int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                        concreteMeasure.ownLang = concreteMeasure.getGlobalFromID(selectedPosition);
                        dialogInterface.dismiss();
                        onSave();
                    })
                    .show();
        }
    }

    private void checkVersion() {
        DatabaseReference ref = mDatabase.child("measures").child(userMeasure.cloudID).child("version");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long version = dataSnapshot.getValue(Long.class);
                if (version != null) {
                    if (version > userMeasure.version) {
                        updateView(versionView, String.format(getString(R.string.new_version), userMeasure.version.toString(), version));
                    } else {
                        updateView(versionView, String.format(getString(R.string.current_version), userMeasure.version.toString()));
                    }
                } else {
                    updateView(versionView, getString(R.string.local_measure));
                }
                SetMeasureActivity.this.version = version;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                Intent intent = new Intent(getApplicationContext(), EditMeasureActivity.class);
                intent.putExtra(EXTRA_MEASURE_FILE_NAME, concreteMeasure.concreteFileName);
                startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
