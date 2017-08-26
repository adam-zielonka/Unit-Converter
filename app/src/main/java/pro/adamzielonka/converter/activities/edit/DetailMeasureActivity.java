package pro.adamzielonka.converter.activities.edit;

import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.models.concrete.CUnit;
import pro.adamzielonka.converter.tools.Language;
import pro.adamzielonka.items.Item;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;

public class DetailMeasureActivity extends EditActivity {

    private DatabaseReference mDatabase;

    private Long version;
    private String versionInfo;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_detail_measure);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.addItems();
        new Item.Builder(this)
                .setTitleHeader(R.string.local_settings)
                .setTitle(R.string.list_own_name_measure)
                .setSwitcherUpdate(() -> cMeasure.isOwnName)
                .setSwitcherAction(isOwnName -> cMeasure.isOwnName = (Boolean) isOwnName)
                .setUpdate(() -> cMeasure.ownName)
                .setAction((String ownName) -> cMeasure.ownName = ownName)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_own_lang_measure)
                .setSwitcherUpdate(() -> cMeasure.isOwnLang)
                .setSwitcherAction(isOwnLang -> cMeasure.isOwnLang = (Boolean) isOwnLang)
                .setUpdate(() -> Language.getLanguage(cMeasure.ownLang))
                .setArray(() -> cMeasure.getGlobalLangs())
                .setPosition(() -> cMeasure.getOwnLangID())
                .setAction((Integer position) -> cMeasure.ownLang = cMeasure.getGlobalFromID(position))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_version)
                .setIf(() -> !measure.cloudID.equals(""))
                .setUpdate(this::getVersionInfo)
                .setAction(this::versionAction)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.measure_details)
                .setTitle(R.string.list_item_name)
                .setUpdate(() -> measure.getName(cMeasure.getOwnLang(this)))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_author)
                .setUpdate(this::getAuthors)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_units)
                .setUpdate(this::getMeasures)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_languages)
                .setUpdate(this::getLanguages)
                .add(itemsView);
    }

    private String getAuthors() {
        StringBuilder builder = new StringBuilder();

        for (String author : measure.author) {
            if (builder.length() != 0) builder.append("\n");
            builder.append(author);
        }

        return builder.toString();
    }

    private String getMeasures() {
        StringBuilder builder = new StringBuilder();

        for (CUnit cUnit : cMeasure.cUnits) {
            if (builder.length() != 0) builder.append("\n");
            builder.append(cUnit.name).append(" - ").append(cUnit.description.get(cMeasure.getOwnLang(this), cMeasure.global));
        }

        return builder.toString();
    }

    private String getLanguages() {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Integer> entry : cMeasure.languages.entrySet()) {
            if (builder.length() != 0) builder.append("\n");
            builder.append(entry.getKey()).append(" - ").append(Language.getLanguage(entry.getKey(), cMeasure.getOwnLang(this)));
        }

        return builder.toString();
    }

    //region version
    public String getVersionInfo() {
        if (!measure.cloudID.equals("")) {
            if (versionInfo == null) {
                checkOnlineVersion();
                versionInfo = measure.version.toString();
            }
        } else versionInfo = getString(R.string.local_measure);
        return versionInfo;
    }

    public void versionAction() {
        if (version != null && version > measure.version) {
            //TODO: download update
        } else if (!measure.cloudID.equals("")) {
            version = measure.version;
            checkOnlineVersion();
        }
    }

    private void checkOnlineVersion() {
        versionInfo = String.format(getString(R.string.checking_version), measure.version.toString());
        itemsView.onSave();

        DatabaseReference ref = mDatabase.child("measures").child(measure.cloudID).child("version");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long versionOnline = dataSnapshot.getValue(Long.class);
                if (versionOnline != null) {
                    if (versionOnline > measure.version)
                        versionInfo = String.format(getString(R.string.new_version),
                                measure.version.toString(), versionOnline);
                    else versionInfo = String.format(getString(R.string.current_version),
                            measure.version.toString());
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
