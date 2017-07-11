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
import pro.adamzielonka.converter.components.MyListView;
import pro.adamzielonka.converter.units.user.Unit;

import static pro.adamzielonka.converter.tools.Open.openConcreteMeasure;
import static pro.adamzielonka.converter.tools.Open.openMeasure;
import static pro.adamzielonka.converter.tools.Open.openUnit;

public class EditDescriptionActivity extends EditActivity implements ListView.OnItemClickListener {

    private String measureFileName;
    private String unitName;

    private View unitDescriptionBaseView;
    private View unitDescriptionPrefixView;

    private Unit unit;

    private static final int EDIT_DESCRIPTION_BASE = 1;
    private static final int EDIT_DESCRIPTION_PREFIX = 2;

    @Override
    public void onLoad() throws FileNotFoundException {
        Intent intent = getIntent();
        measureFileName = intent.getStringExtra("measureFileName");
        unitName = intent.getStringExtra("unitName");

        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);

        MyListView listView = findViewById(R.id.editListView);
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);
        listView.setActivity(this);

        listView.addHeaderTitle(getString(R.string.list_title_description));
        unitDescriptionBaseView = listView.addHeaderItem(getString(R.string.list_item_description_base), unit.getDescription());
        unitDescriptionPrefixView = listView.addHeaderItem(getString(R.string.list_item_description_global_prefix), unit.getDescriptionPrefix());
    }

    @Override
    public void onReload() throws FileNotFoundException {
        concreteMeasure = openConcreteMeasure(this, measureFileName);
        userMeasure = openMeasure(this, concreteMeasure.getUserFileName());
        unit = openUnit(unitName, userMeasure);
        ((TextView) unitDescriptionBaseView.findViewById(R.id.textSecondary)).setText(unit.getDescription());
        ((TextView) unitDescriptionPrefixView.findViewById(R.id.textSecondary)).setText(unit.getDescriptionPrefix());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case EDIT_DESCRIPTION_BASE:
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
                break;
            case EDIT_DESCRIPTION_PREFIX:
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
                break;
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
