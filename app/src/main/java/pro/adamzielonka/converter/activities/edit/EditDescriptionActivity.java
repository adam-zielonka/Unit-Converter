package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.itemsview.classes.Item;

public class EditDescriptionActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_description);
        super.addItems();

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_description)
                .setTitle(R.string.list_item_description_base)
                .setUpdate(() -> userMeasure.getWords(unit.description, userMeasure.global))
                .setAction(text -> unit.description.put(userMeasure.global, (String) text))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_description_global_prefix)
                .setUpdate(() -> userMeasure.getWords(unit.descriptionPrefix, userMeasure.global))
                .setAction(text -> unit.descriptionPrefix.put(userMeasure.global, (String) text))
                .add(itemsView);
    }
}
