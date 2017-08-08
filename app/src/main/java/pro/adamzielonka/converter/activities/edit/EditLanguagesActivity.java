package pro.adamzielonka.converter.activities.edit;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.itemsview.classes.Item;

public class EditLanguagesActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_languages);
        super.addItems();

        ArrayAdapter<String[]> adapter = new MyArrayAdapter<String[]>(getApplicationContext(), getArrayList()) {
            @Override
            public void setView(String[] item, TextView textPrimary, TextView textSecondary) {
                textPrimary.setText(item[0]);
                textSecondary.setText(item[1]);
            }
        };

        new Item.Builder(this)
                .setTitleHeader(R.string.list_item_language_global)
                .setTitle(concreteMeasure.global)
                .setUpdate(() -> concreteMeasure.languages.get(concreteMeasure.global).toString())
                .add(itemsView);
        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_translations)
                .setAdapter(adapter)
                .setUpdate(this::getArrayList)
                .setAction(position -> {
                    String[] strings = adapter.getItem((int) position);
                    language = strings != null ? strings[0] : "en";
                    startEditActivity(EditTranslationActivity.class);
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_add_translation)
                .add(itemsView);
    }

    private ArrayList<String[]> getArrayList() {
        ArrayList<String[]> list = new ArrayList<>();
        for (Map.Entry<String, Integer> e : concreteMeasure.languages.entrySet()) {
            if (!e.getKey().equals(concreteMeasure.global)) {
                String[] s = new String[2];
                s[0] = e.getKey();
                s[1] = e.getValue().toString();
                list.add(s);
            }
        }
        return list;
    }
}
