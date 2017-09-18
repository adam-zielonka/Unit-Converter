package pro.adamzielonka.converter.activities.edit;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.tools.Language;
import pro.adamzielonka.items.Item;

public class EditLanguagesActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_languages);
        super.addItems();

        ArrayAdapter<String[]> adapter = new MyArrayAdapter<String[]>(getApplicationContext(), getArrayList()) {
            @Override
            public void setView(String[] item, TextView textPrimary, TextView textSecondary) {
                textPrimary.setText(item[2]);
                textSecondary.setText(item[1]);
            }
        };

        new Item.Builder(this)
                .setTitleHeader(R.string.list_item_language_global)
                .setTitle(Language.getLanguage(cMeasure.global))
                .setUpdate(() -> cMeasure.languages.get(cMeasure.global).toString())
                .add(itemsView);
        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_translations)
                .setAdapter(adapter)
                .setUpdate(this::getArrayList)
                .setAction((Integer position) -> {
                    String[] strings = adapter.getItem(position);
                    language = strings != null ? strings[0] : "en";
                    startEditActivity(EditTranslationActivity.class);
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_add_translation)
                .setAlertTitle(R.string.lang_put_code)
                .setAction((String code) -> {
                    cMeasure.newLangs.add(code);
                    language = code;
                    startEditActivity(EditTranslationActivity.class);
                })
                .addValidator(symbol -> !symbol.equals(""), getString(R.string.error_lang_code_empty))
                .add(itemsView);
    }

    private ArrayList<String[]> getArrayList() {
        ArrayList<String[]> list = new ArrayList<>();
        for (Map.Entry<String, Integer> e : cMeasure.languages.entrySet()) {
            if (!e.getKey().equals(cMeasure.global)) {
                String[] s = new String[3];
                s[0] = e.getKey();
                s[1] = e.getValue().toString();
                s[2] = Language.getLanguage(e.getKey());
                list.add(s);
            }
        }
        for (String lang : cMeasure.newLangs) {
            if (!lang.equals(cMeasure.global)) {
                boolean exist = false;
                for (String[] oldLang : list) {
                    if (oldLang[0].equals(lang)) exist = true;
                }
                if (!exist) {
                    String[] s = new String[3];
                    s[0] = lang;
                    s[1] = getString(R.string.lang_new);
                    s[2] = Language.getLanguage(lang);
                    list.add(s);
                }
            }
        }
        return list;
    }
}
