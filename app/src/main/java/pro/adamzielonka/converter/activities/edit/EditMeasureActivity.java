package pro.adamzielonka.converter.activities.edit;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.EditActivity;
import pro.adamzielonka.converter.adapters.MyArrayAdapter;
import pro.adamzielonka.converter.models.file.Prefix;
import pro.adamzielonka.converter.models.file.Unit;
import pro.adamzielonka.converter.settings.Language;
import pro.adamzielonka.items.Item;
import pro.adamzielonka.verification.Tests;

import static pro.adamzielonka.converter.settings.Language.getLanguageWords;

public class EditMeasureActivity extends EditActivity {

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_edit_measure);
        super.addItems();
        ArrayAdapter<Unit> adapter = new MyArrayAdapter<Unit>(getApplicationContext(), measure.units) {
            @Override
            public void setView(Unit item, TextView textPrimary, TextView textSecondary) {
                String description = getLanguageWords(item.descriptionPrefix, measure.global)
                        + getLanguageWords(item.description, measure.global);
                String unitName = item.symbol + (!description.isEmpty() ? " - " + description : "");

                StringBuilder prefixes = new StringBuilder("");
                for (Prefix prefix : item.prefixes) {
                    prefixes.append(prefix.symbol).append(item.symbol);
                    prefixes.append(" ");
                }
                textPrimary.setText(unitName);
                textSecondary.setText(prefixes.toString());
            }
        };

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_Measure)
                .setTitle(R.string.list_item_name)
                .setUpdate(() -> measure.getName(measure.global))
                .setAction((String name) -> measure.setName(cMeasure.global, name))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_units_order)
                .setIf(() -> measure.units.size() > 0)
                .setUpdate(() -> cMeasure.getUnitsOrder())
                .setAction(() -> startEditActivity(EditOrderUnitsActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_measure_default_1)
                .setIf(() -> measure.units.size() > 0)
                .setUpdate(() -> cMeasure.cUnits.get(cMeasure.displayFrom).name)
                .setElseUpdate(() -> "")
                .setArray(() -> cMeasure.getUnitsSymbol())
                .setPosition(() -> measure.displayFrom)
                .setAction((Integer id) -> measure.displayFrom = id)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_measure_default_2)
                .setIf(() -> measure.units.size() > 0)
                .setUpdate(() -> cMeasure.cUnits.get(cMeasure.displayTo).name)
                .setElseUpdate(() -> "")
                .setArray(() -> cMeasure.getUnitsSymbol())
                .setPosition(() -> measure.displayTo)
                .setAction((Integer id) -> measure.displayTo = id)
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_language)
                .setTitle(R.string.list_item_language_available)
                .setUpdate(() -> cMeasure.languages.toString())
                .setAction(() -> startEditActivity(EditLanguagesActivity.class))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_language_global)
                .setUpdate(() -> Language.getLanguage(cMeasure.global))
                .setArray(() -> cMeasure.getGlobalLangs())
                .setPosition(() -> cMeasure.getGlobalID())
                .setAction((Integer id) -> measure.global = cMeasure.getGlobalFromID(id))
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.list_title_units)
                .setAdapter(adapter)
                .setUpdateAdapter(() -> measure.units)
                .setActionAdapter((Integer position) -> {
                    unit = adapter.getItem(position);
                    startEditActivity(EditUnitActivity.class);
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.list_item_add_unit)
                .setAction(this::addUnit)
                .addValidator(symbol -> Tests.isUnique(symbol, measure.units), getString(R.string.error_symbol_unit_already_exist))
                .addValidator(symbol -> !symbol.equals(""), getString(R.string.error_symbol_empty))
                .add(itemsView);
    }

    private void addUnit(String symbol) {
        Unit unitTemp = unit = new Unit();
        unit.symbol = symbol;
        measure.units.add(unit);
        itemsView.onSave();
        unit = unitTemp;
        startEditActivity(EditUnitActivity.class);
    }

}
