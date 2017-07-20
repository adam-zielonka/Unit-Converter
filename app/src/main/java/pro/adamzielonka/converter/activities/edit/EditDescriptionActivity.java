package pro.adamzielonka.converter.activities.edit;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

public class EditDescriptionActivity extends EditActivity implements ListView.OnItemClickListener {

    private View unitEditBaseView;
    private View unitEditPrefixView;

    @Override
    public void onLoad() throws Exception {
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_description));
        unitEditBaseView = listView.addHeaderItem(getString(R.string.list_item_description_base));
        unitEditPrefixView = listView.addHeaderItem(getString(R.string.list_item_description_global_prefix));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();
        updateView(unitEditBaseView, unit.getDescription());
        updateView(unitEditPrefixView, unit.getDescriptionPrefix());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(unitEditBaseView)) {
            EditText editText = getDialogEditText(unit.getDescription());
            getAlertDialogSave(R.string.dialog_unit_description_base, editText.getRootView(), (dialog, which) -> {
                unit.setDescription(editText.getText().toString());
                onSave();
            }).show();

        } else if (view.equals(unitEditPrefixView)) {
            EditText editText = getDialogEditText(unit.getDescriptionPrefix());
            getAlertDialogSave(R.string.dialog_unit_description_global_prefix, editText.getRootView(), (dialog, which) -> {
                unit.setDescriptionPrefix(editText.getText().toString());
                onSave();
            }).show();
        }
    }
}
