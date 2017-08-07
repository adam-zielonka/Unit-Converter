package pro.adamzielonka.converter.activities.edit;

import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.items.classes.Item;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;

public class SetMeasureActivity extends EditActivity {

    private DatabaseReference mDatabase;

    private Long version;
    private String versionInfo;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_set_measure);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.addItems();
        new Item.Builder(this)
                .setTitle(R.string.list_item_author)
                .setUpdate(() -> userMeasure.author)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_version)
                .setIf(() -> !userMeasure.cloudID.equals(""))
                .setUpdate(this::getVersionInfo)
                .setAction(this::versionAction)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.local_settings)
                .setTitle(R.string.list_own_name_measure)
                .setSwitcherUpdate(() -> concreteMeasure.isOwnName)
                .setSwitcherAction(isOwnName -> concreteMeasure.isOwnName = (Boolean) isOwnName)
                .setUpdate(() -> concreteMeasure.ownName)
                .setAction(ownName -> concreteMeasure.ownName = (String) ownName)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_own_lang_measure)
                .setSwitcherUpdate(() -> concreteMeasure.isOwnLang)
                .setSwitcherAction(isOwnLang -> concreteMeasure.isOwnLang = (Boolean) isOwnLang)
                .setUpdate(() -> concreteMeasure.ownLang)
                .setArray(() -> concreteMeasure.getGlobalLangs())
                .setPosition(() -> concreteMeasure.getOwnLangID())
                .setAction(position -> concreteMeasure.ownLang = concreteMeasure.getGlobalFromID((Integer) position))
                .add(itemsView);
    }

    //region version
    public String getVersionInfo() {
        if (!userMeasure.cloudID.equals("")) {
            if (versionInfo == null) {
                checkOnlineVersion();
                versionInfo = userMeasure.version.toString();
            }
        } else versionInfo = getString(R.string.local_measure);
        return versionInfo;
    }

    public void versionAction() {
        if (version != null && version > userMeasure.version) {
            //TODO: download update
        } else if (!userMeasure.cloudID.equals("")) {
            version = userMeasure.version;
            checkOnlineVersion();
        }
    }

    private void checkOnlineVersion() {
        versionInfo = String.format(getString(R.string.checking_version), userMeasure.version.toString());
        itemsView.onSave();

        DatabaseReference ref = mDatabase.child("measures").child(userMeasure.cloudID).child("version");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long versionOnline = dataSnapshot.getValue(Long.class);
                if (versionOnline != null) {
                    if (versionOnline > userMeasure.version)
                        versionInfo = String.format(getString(R.string.new_version),
                                userMeasure.version.toString(), versionOnline);
                    else versionInfo = String.format(getString(R.string.current_version),
                            userMeasure.version.toString());
                } else versionInfo = getString(R.string.local_measure);
                version = versionOnline;
                itemsView.onSave();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //endregion

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
        }

        return super.onOptionsItemSelected(item);
    }
}
