package pro.adamzielonka.converter.activities.edit;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.LanguagesAdapter;

import static pro.adamzielonka.converter.tools.Code.REQUEST_EDIT_ACTIVITY;

public class EditLanguagesActivity extends EditActivity implements ListView.OnItemClickListener {

    private LanguagesAdapter adapter;
    private View globalView;
    private View addTranslationView;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_edit_languages);
        super.onLoad();
        ArrayList<String[]> list = new ArrayList<>();
        for (Map.Entry<String, Integer> e : concreteMeasure.languages.entrySet()) {
            if (!e.getKey().equals(concreteMeasure.global)) {
                String[] s = new String[2];
                s[0] = e.getKey();
                s[1] = e.getValue().toString();
                list.add(s);
            }
        }
        adapter = new LanguagesAdapter(getApplicationContext(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        listView.addItemTitle(getString(R.string.list_item_language_global));
        globalView = listView.addItem(concreteMeasure.global);
        listView.addItemTitle(getString(R.string.list_title_translations));
        addTranslationView = listView.addFooterItem(getString(R.string.list_item_add_translation));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(globalView, concreteMeasure.global, concreteMeasure.languages.get(concreteMeasure.global).toString());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (isAdapterItemClick(position)) {
            String[] strings = adapter.getItem(getAdapterPosition(position));
            language = strings != null ? strings[0] : "en";
            startActivityForResult(setEditIntent(EditTranslationActivity.class), REQUEST_EDIT_ACTIVITY);
        }
    }
}
