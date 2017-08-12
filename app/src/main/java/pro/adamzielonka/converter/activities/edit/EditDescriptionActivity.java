package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.itemsview.Item;

public class EditDescriptionActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_description);
        super.addItems();

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_description)
                .setTitle(R.string.list_item_description_base)
                .setUpdate(() -> measure.getWords(unit.description, measure.global))
                .setAction(text -> unit.description.put(measure.global, (String) text))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_description_global_prefix)
                .setUpdate(() -> measure.getWords(unit.descriptionPrefix, measure.global))
                .setAction(text -> unit.descriptionPrefix.put(measure.global, (String) text))
                .add(itemsView);
    }
}
