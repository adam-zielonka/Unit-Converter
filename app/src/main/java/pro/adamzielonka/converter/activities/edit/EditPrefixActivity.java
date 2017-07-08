package pro.adamzielonka.converter.activities.edit;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.OrderAdapter;
import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Check.checkSymbolPrefixExist;
import static pro.adamzielonka.converter.tools.ListItems.getItemHeader;
import static pro.adamzielonka.converter.tools.ListItems.getItemNormal;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;
import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;
import static pro.adamzielonka.converter.tools.Open.openPrefix;
import static pro.adamzielonka.converter.tools.Open.openUnit;

public class EditPrefixActivity extends EditActivity implements ListView.OnItemClickListener {

    private Unit unit;
    private Prefix prefix;

    private String measureFileName;
    private String unitName;
    private String prefixName;

    private View prefixNameView;
    private View prefixDescriptionView;
    private View prefixExponentView;

    private static final int EDIT_SYMBOL = 1;
    private static final int EDIT_DESCRIPTION = 2;
    private static final int EDIT_EXP = 3;

    @Override
    public void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");
        unitName = intent.getStringExtra("unitName");
        prefixName = intent.getStringExtra("prefixName");

        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        prefix = openPrefix(prefixName, unit);
        ListView listView = findViewById(R.id.editListView);
        listView.setAdapter(new OrderAdapter(this, (new ArrayList<>())));
        listView.setOnItemClickListener(this);

        prefixNameView = getItemNormal(this, getString(R.string.list_item_symbol), prefix.getPrefixName());
        prefixDescriptionView = getItemNormal(this, getString(R.string.list_item_description), prefix.getPrefixDescription());
        prefixExponentView = getItemNormal(this, getString(R.string.list_item_exponent), doubleToString(prefix.getPrefixExponent()));

        listView.addHeaderView(getItemHeader(this, getString(R.string.list_title_prefix)), false, false);
        listView.addHeaderView(prefixNameView, false, true);
        listView.addHeaderView(prefixDescriptionView, false, true);
        listView.addHeaderView(prefixExponentView, false, true);
    }

    @Override
    public void onReload() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        prefix = openPrefix(prefixName, unit);
        ((TextView) prefixNameView.findViewById(R.id.textSecondary)).setText(prefix.getPrefixName());
        ((TextView) prefixDescriptionView.findViewById(R.id.textSecondary)).setText(prefix.getPrefixDescription());
        ((TextView) prefixExponentView.findViewById(R.id.textSecondary)).setText(doubleToString(prefix.getPrefixExponent()));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case EDIT_SYMBOL:
                View layout = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                final EditText editText = layout.findViewById(R.id.editText);
                editText.setText(prefix.getPrefixName());
                editText.setSelection(editText.length());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_prefix_symbol)
                        .setView(layout)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                            String newName = editText.getText().toString();
                            if (!newName.equals(prefixName)) {
                                if (!checkSymbolPrefixExist(newName, unit.getPrefixes())) {
                                    prefix.setPrefixName(newName);
                                    prefixName = newName;
                                    onSave();
                                } else {
                                    showError(this, R.string.error_symbol_prefix_already_exist);
                                }
                            }
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                break;
            case EDIT_DESCRIPTION:
                View layoutDescription = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                final EditText editTextDescription = layoutDescription.findViewById(R.id.editText);
                editTextDescription.setText(prefix.getPrefixDescription());
                editTextDescription.setSelection(editTextDescription.length());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_prefix_description)
                        .setView(layoutDescription)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                            prefix.setPrefixDescription(editTextDescription.getText().toString());
                            onSave();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                break;
            case EDIT_EXP:
                View layoutExp = getLayoutInflater().inflate(R.layout.layout_dialog_edit_text, null);
                final EditText editTextExp = layoutExp.findViewById(R.id.editText);
                editTextExp.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED);
                editTextExp.setText(doubleToString(prefix.getPrefixExponent()));
                editTextExp.setSelection(editTextExp.length());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_prefix_exponent)
                        .setView(layoutExp)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_save, (dialog, which) -> {
                            prefix.setPrefixExponent(stringToDouble(editTextExp.getText().toString()));
                            onSave();
                        }).setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                }).show();
                break;
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EditUnitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("measureFileName", concreteMeasure.getConcreteFileName());
        intent.putExtra("unitName", unit.getUnitName());
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
