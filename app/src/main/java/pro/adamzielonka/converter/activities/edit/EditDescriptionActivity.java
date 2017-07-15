package pro.adamzielonka.converter.activities.edit;

import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import pro.adamzielonka.converter.R;

public class EditDescriptionActivity extends EditActivity implements ListView.OnItemClickListener {

    private View unitEditBaseView;
    private View unitEditPrefixView;

    @Override
    public void onLoad() throws FileNotFoundException {
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.list_title_description));
        unitEditBaseView = listView.addHeaderItem(getString(R.string.list_item_description_base), unit.getDescription());
        unitEditPrefixView = listView.addHeaderItem(getString(R.string.list_item_description_global_prefix), unit.getDescriptionPrefix());
    }

    @Override
    public void onReload() throws FileNotFoundException {
        super.onReload();
        ((TextView) unitEditBaseView.findViewById(R.id.textSecondary)).setText(unit.getDescription());
        ((TextView) unitEditPrefixView.findViewById(R.id.textSecondary)).setText(unit.getDescriptionPrefix());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(unitEditBaseView)) {
            View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            EditText editText = getDialogEditText(layout, unit.getDescription());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_unit_description_base)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setDescription(editText.getText().toString());
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(unitEditPrefixView)) {
            View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            final EditText editText = getDialogEditText(layout, unit.getDescriptionPrefix());
            editText.setText(unit.getDescriptionPrefix());
            editText.setSelection(editText.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_unit_description_global_prefix)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setDescriptionPrefix(editText.getText().toString());
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        }
    }

    @Override
    public void onBackPressed() {
        setResult(resultCode);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
