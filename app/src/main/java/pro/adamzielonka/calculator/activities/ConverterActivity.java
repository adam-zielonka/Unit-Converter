package pro.adamzielonka.calculator.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.abstractes.BaseActivity;
import pro.adamzielonka.calculator.adapters.UnitsAdapter;
import pro.adamzielonka.calculator.interfaces.IConverter;
import pro.adamzielonka.calculator.units.Units;

public class ConverterActivity extends BaseActivity {

    private EditText resultOutput;
    private EditText resultConverter;
    private Spinner spinnerFromConverter;
    private Spinner spinnerToConverter;
    private IConverter converter;
    private String[] arrayItems;
    private String[] arrayUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        Intent intent = getIntent();

        converterSetUp(intent.getStringExtra("converterName"), intent.getStringExtra("converterType"));

        resultOutput = (EditText) findViewById(R.id.resultOutput);
        resultConverter = (EditText) findViewById(R.id.resultConverter);

        resultOutput.setOnFocusChangeListener(mResultOnClickListener);
        resultConverter.setOnFocusChangeListener(mResultOnClickListener);

        UnitsAdapter unitsAdapter = new UnitsAdapter(getApplicationContext(), arrayItems, arrayUnits);

        spinnerFromConverter = (Spinner) findViewById(R.id.spinnerFromConverter);
        spinnerToConverter = (Spinner) findViewById(R.id.spinnerToConverter);

        spinnerFromConverter.setAdapter(unitsAdapter);
        spinnerToConverter.setAdapter(unitsAdapter);

        spinnerFromConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerToConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerToConverter.setSelection(1);
    }

    private void converterSetUp(String converterName, String converterType) {
        try {
            if (converterType.equals("json")) {
                Intent intent = getIntent();
                int nav_id = intent.getIntExtra("converterNavId", 0);
                Units units = unitsList.get(nav_id - 1000);
                converter = units;

                setTitle(units.getName());
                mNavigationView.setCheckedItem(nav_id);
                mItemId = nav_id;

                arrayItems = unitsList.get(nav_id - 1000).getArrayUnitsName();
                arrayUnits = unitsList.get(nav_id - 1000).getArrayUnitsDescription();
            } else {
                String className = PACKAGE_NAME + ".converters." + converterName + "Converter";
                Class cls = Class.forName(className);
                converter = (IConverter) cls.newInstance();
                String name = converterName.toLowerCase();
                setTitle(getIdResourceByName("string", "title_converter_" + name));
                mNavigationView.setCheckedItem(getIdResourceByName("id", "nav_" + name));
                mItemId = getIdResourceByName("id", "nav_" + name);
                Resources res = getResources();
                arrayItems = res.getStringArray(getIdResourceByName("array", name + "Items"));
                arrayUnits = res.getStringArray(getIdResourceByName("array", name + "Units"));
            }


        } catch (Exception e) {
            converterSetUp("Byte", "json");
        }
    }

    private final View.OnFocusChangeListener mResultOnClickListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (resultConverter.equals(v)) {
                    swapResult();
                }
            }
        }
    };

    private void swapResult() {
        EditText resultTemp = resultOutput;
        resultOutput = resultConverter;
        resultConverter = resultTemp;

        Spinner spinnerTemp = spinnerFromConverter;
        spinnerFromConverter = spinnerToConverter;
        spinnerToConverter = spinnerTemp;
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
        double result = converter.calculate(
                convertStringToDouble(resultOutput.getText().toString()),
                spinnerFromConverter.getSelectedItem().toString(),
                spinnerToConverter.getSelectedItem().toString()
        );
        resultConverter.setText(convertDoubleToString(result));
    }

    public void onClickDigit(View v) {
        int maxDigitCount = 15;
        if (resultOutput.getText().length() >= maxDigitCount) return;
        if (resultOutput.getText().toString().equals("0"))
            resultOutput.setText("");
        if (resultOutput.getText().toString().equals("-0"))
            resultOutput.setText("-");
        resultOutput.append(v.getTag().toString());
        if (!resultOutput.getText().toString().contains(","))
            resultOutput.setText(prepareString(resultOutput.getText().toString()));
        calculateAndPrintResult();
    }

    public void onClickComa(View v) {
        if (!resultOutput.getText().toString().contains(","))
            resultOutput.append(",");
    }

    public void onClickSingleOperator(View v) {
        double result = converter.singleCalculate(
                convertStringToDouble(resultOutput.getText().toString()),
                v.getTag().toString()
        );
        resultOutput.setText(convertDoubleToString(result));
        calculateAndPrintResult();
    }

    public void onClickClearOutput(View v) {
        resultOutput.setText("0");
        resultConverter.setText("0");
        calculateAndPrintResult();
    }

    public void onClickDeleteLast(View v) {
        resultOutput.setText(resultOutput.getText().toString().substring(0, resultOutput.getText().toString().length() - 1));
        if (resultOutput.getText().toString().isEmpty())
            resultOutput.setText("0");
        resultOutput.setText(prepareString(resultOutput.getText().toString()));
        calculateAndPrintResult();
    }

}
