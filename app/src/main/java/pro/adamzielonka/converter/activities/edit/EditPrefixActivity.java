package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.tools.Test;
import pro.adamzielonka.converter.tools.Tests;

public class EditPrefixActivity extends EditActivity {

    @Override
    protected void addItems() {
        setTitle(R.string.title_activity_edit_prefix);

        addItemTitle(R.string.list_title_prefix);
        addItemText(R.string.list_item_symbol, () -> prefix.symbol, symbol -> prefix.symbol = prefixName = symbol,
                new Test(symbol -> Tests.isUnique(symbol, unit.prefixes), R.string.error_symbol_prefix_already_exist));
        addItemText(R.string.list_item_description, () -> userMeasure.getWords(prefix.description, userMeasure.global),
                text -> prefix.description.put(userMeasure.global, text));
        addItemNumber(R.string.list_item_exponent, () -> prefix.exp, exp -> prefix.exp = exp);
    }

    @Override
    protected void addActions() {
        addActionDelete(R.string.delete_prefix_title, () -> unit.prefixes.remove(prefix));
    }
}
