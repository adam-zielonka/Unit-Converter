package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.tools.Item;
import pro.adamzielonka.converter.tools.Tests;

public class EditPrefixActivity extends EditActivity {

    @Override
    protected void addItems() {
        setTitle(R.string.title_activity_edit_prefix);

        Item.Builder(R.string.list_title_prefix).add(this);
        Item.Builder(R.string.list_item_symbol)
                .update(() -> prefix.symbol)
                .alert(symbol -> prefix.symbol = prefixName = (String) symbol)
                .validate(symbol -> Tests.isUnique(symbol, unit.prefixes), R.string.error_symbol_prefix_already_exist)
                .add(this);
        Item.Builder(R.string.list_item_description)
                .update(() -> userMeasure.getWords(prefix.description, userMeasure.global))
                .alert(text -> prefix.description.put(userMeasure.global, (String) text)).add(this);
        Item.Builder(R.string.list_item_exponent)
                .update(() -> prefix.exp)
                .alert(exp -> prefix.exp = (Double) exp).add(this);
    }

    @Override
    protected void addActions() {
        addActionDelete(R.string.delete_prefix_title, () -> unit.prefixes.remove(prefix));
    }
}
