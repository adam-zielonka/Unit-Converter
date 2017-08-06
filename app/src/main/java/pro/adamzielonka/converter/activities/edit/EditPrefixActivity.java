package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.items.classes.Item;
import pro.adamzielonka.items.tools.Tests;

public class EditPrefixActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_prefix);
        super.addItems();

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_prefix)
                .setTitle(R.string.list_item_symbol)
                .setUpdate(() -> prefix.symbol)
                .setAction(symbol -> prefix.symbol = prefixName = (String) symbol)
                .addValidator(symbol -> Tests.isUnique(symbol, unit.prefixes), getString(R.string.error_symbol_prefix_already_exist))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_description)
                .setUpdate(() -> userMeasure.getWords(prefix.description, userMeasure.global))
                .setAction(text -> prefix.description.put(userMeasure.global, (String) text))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_exponent)
                .setUpdate(() -> prefix.exp)
                .setAction(exp -> prefix.exp = (Double) exp)
                .add(itemsView);
    }
//
//    @Override
//    protected void addActions() {
//        addActionDelete(R.string.delete_prefix_title, () -> unit.prefixes.remove(prefix));
//    }
}
