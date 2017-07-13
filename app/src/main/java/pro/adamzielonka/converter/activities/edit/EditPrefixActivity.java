package pro.adamzielonka.converter.activities.edit;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import pro.adamzielonka.converter.R;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public class EditPrefixActivity extends EditActivity implements ListView.OnItemClickListener {

    private View prefixNameView;
    private View prefixDescriptionView;
    private View prefixExponentView;

    @Override
    public void onLoad() throws FileNotFoundException {
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);
        listView.setActivity(this);

        listView.addHeaderTitle(getString(R.string.list_title_prefix));
        prefixNameView = listView.addHeaderItem(getString(R.string.list_item_symbol), prefix.getSymbol());
        prefixDescriptionView = listView.addHeaderItem(getString(R.string.list_item_description), prefix.getDescription());
        prefixExponentView = listView.addHeaderItem(getString(R.string.list_item_exponent), doubleToString(prefix.getExp()));
    }

    @Override
    public void onReload() throws FileNotFoundException {
        super.onReload();
        ((TextView) prefixNameView.findViewById(R.id.textSecondary)).setText(prefix.getSymbol());
        ((TextView) prefixDescriptionView.findViewById(R.id.textSecondary)).setText(prefix.getDescription());
        ((TextView) prefixExponentView.findViewById(R.id.textSecondary)).setText(doubleToString(prefix.getExp()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(prefixNameView)) {
            View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            final EditText editText = layout.findViewById(R.id.editText);
            editText.setText(prefix.getSymbol());
            editText.setSelection(editText.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_prefix_symbol)
                    .setView(layout)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        String newName = editText.getText().toString();
                        if (!newName.equals(prefixName)) {
                            if (!isSymbolPrefixExist(newName, unit.getPrefixes())) {
                                prefix.setSymbol(newName);
                                prefixName = newName;
                                onSave();
                            } else {
                                showError(this, R.string.error_symbol_prefix_already_exist);
                            }
                        }
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(prefixDescriptionView)) {
            View layoutDescription = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            final EditText editTextDescription = layoutDescription.findViewById(R.id.editText);
            editTextDescription.setText(prefix.getDescription());
            editTextDescription.setSelection(editTextDescription.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_prefix_description)
                    .setView(layoutDescription)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        prefix.setDescription(editTextDescription.getText().toString());
                        onSave();
                    }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            }).show();

        } else if (view.equals(prefixExponentView)) {
            View layoutExp = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
            final EditText editTextExp = layoutExp.findViewById(R.id.editText);
            editTextExp.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
            editTextExp.setText(doubleToString(prefix.getExp()));
            editTextExp.setSelection(editTextExp.length());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_prefix_exponent)
                    .setView(layoutExp)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                        prefix.setExp(stringToDouble(editTextExp.getText().toString()));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_unit_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                            unit.getPrefixes().remove(prefix);
                            onSave(false);
                            onBackPressed();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
