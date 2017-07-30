package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

public class EditDescriptionActivity extends EditActivity {

    @Override
    protected void addItems() {
        setTitle(R.string.title_activity_edit_description);

        addItemTitle(R.string.list_title_description);
        addItemText(R.string.list_item_description_base, () -> userMeasure.getWords(unit.description, userMeasure.global),
                text -> unit.description.put(userMeasure.global, text));
        addItemText(R.string.list_item_description_global_prefix, () -> userMeasure.getWords(unit.descriptionPrefix, userMeasure.global),
                text -> unit.descriptionPrefix.put(userMeasure.global, text));
    }
}
