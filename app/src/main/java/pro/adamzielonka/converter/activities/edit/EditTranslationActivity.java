package pro.adamzielonka.converter.activities.edit;

import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.items.Item;
import pro.adamzielonka.items.dialog.EditDialogBuilder;

public class EditTranslationActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_translation);
        super.addItems();

        ArrayAdapter<String[]> adapter = new MyArrayAdapter<String[]>(
                getApplicationContext(), measure.getLanguagesStr(this, language)) {
            @Override
            public void setView(String[] item, TextView textPrimary, TextView textSecondary) {
                textPrimary.setText(item[0]);
                textSecondary.setText(item[1]);
            }
        };

        new Item.Builder(this)
                .setAdapter(adapter)
                .setUpdate(() -> measure.getLanguagesStr(this, language))
                .setAction((Integer position) -> {
                    String[] item = adapter.getItem(position);
                    if (item != null) editTranslation(item);
                })
                .add(itemsView);
    }

    private void editTranslation(String[] item) {
        new EditDialogBuilder(this)
                .setValue(item[1].equals(getString(R.string.language_repeat_tag)) ? item[0] : item[1].equals(getString(R.string.language_empty_tag)) ? "" : item[1])
                .addValidator(text -> !text.equals(getString(R.string.language_repeat_tag)) && !text.equals(getString(R.string.language_empty_tag)), getString(R.string.error_not_allowed))
                .setAction(text -> {
                    measure.setLanguagesStr(language, Integer.parseInt(item[2]), (String) text);
                    itemsView.onSave();
                })
                .setNeutralAction(R.string.translation_set_repeat, (d, i) -> {
                    measure.setRepeatStr(language, Integer.parseInt(item[2]));
                    itemsView.onSave();
                })
                .setTitle(item[0])
                .create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_translation_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_delete, (d, i) -> {
                            measure.removeLang(language);
                            if (cMeasure.ownLang.equals(language))
                                cMeasure.ownLang = cMeasure.global;
                            cMeasure.newLangs.remove(language);
                            onSave();
                            onBackPressed();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
