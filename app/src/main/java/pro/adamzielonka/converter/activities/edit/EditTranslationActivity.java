package pro.adamzielonka.converter.activities.edit;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.LanguagesAdapter;

public class EditTranslationActivity extends EditActivity implements ListView.OnItemClickListener {

    private LanguagesAdapter adapter;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_edit_translation);
        super.onLoad();
        adapter = new LanguagesAdapter(getApplicationContext(),
                userMeasure.getLanguagesStr(this, language));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

    }
}
