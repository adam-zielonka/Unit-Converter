package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
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
        listView.setActivity(this);

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
            final EditText editText = layout.findViewById(R.id.editText);
            editText.setText(unit.getDescription());
            editText.setSelection(editText.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_unit_symbol)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setDescription(editText.getText().toString());
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(unitEditPrefixView)) {
            View layoutPrefix = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            final EditText editTextPrefix = layoutPrefix.findViewById(R.id.editText);
            editTextPrefix.setText(unit.getDescriptionPrefix());
            editTextPrefix.setSelection(editTextPrefix.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_unit_symbol)
                    .setView(layoutPrefix)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        unit.setDescriptionPrefix(editTextPrefix.getText().toString());
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditUnitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit.getSymbol());
        startActivity(intent);
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
