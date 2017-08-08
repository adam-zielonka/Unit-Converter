package pro.adamzielonka.converter.activities.edit;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.itemsview.classes.Item;

public class EditTranslationActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_translation);
        super.addItems();

        ArrayAdapter<String[]> adapter = new MyArrayAdapter<String[]>(getApplicationContext(),
                userMeasure.getLanguagesStr(this, language)) {
            @Override
            public void setView(String[] item, TextView textPrimary, TextView textSecondary) {
                textPrimary.setText(item[0]);
                textSecondary.setText(item[1]);
            }
        };

        new Item.Builder(this)
                .setAdapter(adapter)
                .setUpdate(() -> userMeasure.getLanguagesStr(this, language))
                .add(itemsView);
    }
}
