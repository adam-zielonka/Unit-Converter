package pro.adamzielonka.converter.activities.edit;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.models.concrete.CUnit;
import pro.adamzielonka.converter.tools.Language;
import pro.adamzielonka.items.Item;

import static pro.adamzielonka.converter.names.Code.REQUEST_EDIT_ACTIVITY;

public class DetailMeasureActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_detail_measure);
        super.addItems();
        new Item.Builder(this)
                .setTitleHeader(R.string.local_settings)
                .setTitle(R.string.list_own_name_measure)
                .setSwitcherUpdate(() -> cMeasure.isOwnName)
                .setSwitcherAction(isOwnName -> cMeasure.isOwnName = isOwnName)
                .setUpdate(() -> cMeasure.ownName)
                .setAction((String ownName) -> cMeasure.ownName = ownName)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_own_lang_measure)
                .setSwitcherUpdate(() -> cMeasure.isOwnLang)
                .setSwitcherAction(isOwnLang -> cMeasure.isOwnLang = isOwnLang)
                .setUpdate(() -> Language.getLanguage(cMeasure.ownLang))
                .setArray(() -> cMeasure.getGlobalLangs())
                .setPosition(() -> cMeasure.getOwnLangID())
                .setAction((Integer position) -> cMeasure.ownLang = cMeasure.getGlobalFromID(position))
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
