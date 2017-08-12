package pro.adamzielonka.converter.activities.edit;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.itemsview.Item;

public class EditTranslationItemActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_translation);
        super.addItems();

        ArrayAdapter<String[]> adapter = new MyArrayAdapter<String[]>(getApplicationContext(),
                measure.getLanguagesStr(this, language)) {
            @Override
            public void setView(String[] item, TextView textPrimary, TextView textSecondary) {
                textPrimary.setText(item[0]);
                textSecondary.setText(item[1]);
            }
        };

        new Item.Builder(this)
                .setAdapter(adapter)
                .setUpdate(() -> measure.getLanguagesStr(this, language))
                .setAction(position -> {

                })
                .add(itemsView);
    }
}
