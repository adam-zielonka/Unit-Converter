package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.items.Builder;
import pro.adamzielonka.items.HeaderItemBuilder;

public class EditDescriptionActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_description);
        super.addItems();

        new HeaderItemBuilder(this).setTitle(R.string.list_title_description).add(itemsView);
        new Builder(this)
                .setTitle(R.string.list_item_description_base)
                .setUpdate(() -> measure.getWords(unit.description, measure.global))
                .setAction((String text) -> unit.description.put(measure.global, text))
                .add(itemsView);
        new Builder(this)
                .setTitle(R.string.list_item_description_global_prefix)
                .setUpdate(() -> measure.getWords(unit.descriptionPrefix, measure.global))
                .setAction((String text) -> unit.descriptionPrefix.put(measure.global, text))
                .add(itemsView);
    }
}
