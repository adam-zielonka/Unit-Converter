package pro.adamzielonka.converter.activities.edit;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class EditPrefixActivity extends EditActivity implements ListView.OnItemClickListener {

    private View prefixNameView;
    private View prefixDescriptionView;
    private View prefixExponentView;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_edit_prefix);
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_prefix));
        prefixNameView = listView.addHeaderItem(getString(R.string.list_item_symbol));
        prefixDescriptionView = listView.addHeaderItem(getString(R.string.list_item_description));
        prefixExponentView = listView.addHeaderItem(getString(R.string.list_item_exponent));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(prefixNameView, prefix.symbol);
        updateView(prefixDescriptionView, userMeasure.getWords(prefix.description, userMeasure.global));
        updateView(prefixExponentView, doubleToString(prefix.exp));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(prefixNameView)) {
            newAlertDialogTextExist(R.string.dialog_prefix_symbol, prefix.symbol,
                    this::isSymbolPrefixExist, unit.prefixes, R.string.error_symbol_prefix_already_exist,
                    newName -> prefix.symbol = prefixName = newName);

        } else if (view.equals(prefixDescriptionView)) {
            newAlertDialogText(R.string.dialog_prefix_description, userMeasure.getWords(prefix.description, userMeasure.global),
                    string -> prefix.description.put(userMeasure.global, string));

        } else if (view.equals(prefixExponentView)) {
            newAlertDialogNumber(R.string.dialog_prefix_exponent, prefix.exp, exp -> prefix.exp = exp);
        }
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
                newAlertDialogDelete(R.string.delete_prefix_title, () -> unit.prefixes.remove(prefix));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
