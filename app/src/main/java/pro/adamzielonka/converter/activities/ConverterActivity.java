package pro.adamzielonka.converter.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.UnitsAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.Units;

import static pro.adamzielonka.converter.tools.Number.appendComa;
import static pro.adamzielonka.converter.tools.Number.appendDigit;
import static pro.adamzielonka.converter.tools.Number.changeSign;
import static pro.adamzielonka.converter.tools.Number.convertDoubleToString;
import static pro.adamzielonka.converter.tools.Number.convertStringToDouble;
import static pro.adamzielonka.converter.tools.Number.deleteLast;

public class ConverterActivity extends BaseActivity {

    private EditText textFrom;
    private EditText textTo;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Units converter;
    private String[][] arrayUnits;
    private UnitsAdapter unitsAdapter;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getConverterStyleID(preferences.getString("theme", "")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        converterSetUp(getIntent().getIntExtra("converterNavId", 1000));

        textFrom = (EditText) findViewById(R.id.textFrom);
        textTo = (EditText) findViewById(R.id.textTo);

        textFrom.setOnFocusChangeListener(mResultOnClickListener);
        textTo.setOnFocusChangeListener(mResultOnClickListener);

        unitsAdapter = new UnitsAdapter(getApplicationContext(), arrayUnits);

        spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) findViewById(R.id.spinnerTo);

        spinnerFrom.setAdapter(unitsAdapter);
        spinnerTo.setAdapter(unitsAdapter);

        spinnerFrom.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerTo.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerFrom.setSelection(converter.getDisplayFrom());
        spinnerTo.setSelection(converter.getDisplayTo());
    }

    private void converterSetUp(int converterNavId) {
        try {
            mItemId = converterNavId;
            converter = unitsList.get(mItemId - 1000);

            setTitle(converter.getName());
            mNavigationView.setCheckedItem(mItemId);

            arrayUnits = unitsList.get(mItemId - 1000).getArrayUnits();
        } catch (Exception e) {
            converterSetUp(1000);
        }
    }

    private final View.OnFocusChangeListener mResultOnClickListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (textTo.equals(v)) {
                    swapTexts();
                    swapSpinners();
                }
            }
        }
    };

    private void swapTexts() {
        EditText textTemp = textFrom;
        textFrom = textTo;
        textTo = textTemp;
    }

    private void swapSpinners() {
        Spinner spinnerTemp = spinnerFrom;
        spinnerFrom = spinnerTo;
        spinnerTo = spinnerTemp;
    }

    private final AdapterView.OnItemSelectedListener mSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            calculateAndPrintResult();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };

    private void calculateAndPrintResult() {
        int fromId = (int) spinnerFrom.getSelectedItemId();
        int toId = (int) spinnerTo.getSelectedItemId();
        double result = converter.calculate(
                convertStringToDouble(textFrom.getText().toString()),
                unitsAdapter.getItemName(fromId),
                unitsAdapter.getItemName(toId)
        );
        textTo.setText(convertDoubleToString(result));
    }

    public void onClickDigit(View v) {
        textFrom.setText(appendDigit(textFrom.getText().toString(), v.getTag().toString()));
        calculateAndPrintResult();
    }

    public void onClickComa(View v) {
        textFrom.setText(appendComa(textFrom.getText().toString()));
    }

    public void onClickChangeSign(View v) {
        textFrom.setText(changeSign(textFrom.getText().toString()));
        calculateAndPrintResult();
    }

    public void onClickClear(View v) {
        textFrom.setText("0");
        calculateAndPrintResult();
    }

    public void onClickDeleteLast(View v) {
        textFrom.setText(deleteLast(textFrom.getText().toString()));
        calculateAndPrintResult();
    }
}
