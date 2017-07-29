package pro.adamzielonka.converter.activities.edit;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;

public class EditDescriptionActivity extends EditActivity implements ListView.OnItemClickListener {

    private View unitEditBaseView;
    private View unitEditPrefixView;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_edit_description);
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
        updateView(unitEditBaseView, userMeasure.getWords(unit.description, userMeasure.global));
        updateView(unitEditPrefixView, userMeasure.getWords(unit.descriptionPrefix, userMeasure.global));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(unitEditBaseView)) {
            newAlertDialogText(R.string.dialog_unit_description_base, userMeasure.getWords(unit.description, userMeasure.global),
                    text -> unit.description.put(userMeasure.global, text));

        } else if (view.equals(unitEditPrefixView)) {
            newAlertDialogText(R.string.dialog_unit_description_global_prefix, userMeasure.getWords(unit.descriptionPrefix, userMeasure.global),
                    text -> unit.descriptionPrefix.put(userMeasure.global, text));
        }
    }
}
