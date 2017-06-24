package pro.adamzielonka.calculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.adapters.UnitsAdapter;
import pro.adamzielonka.calculator.units.Units;

public class ConverterActivity extends BaseActivity {

    private EditText resultOutput;
    private EditText resultConverter;
    private Spinner spinnerFromConverter;
    private Spinner spinnerToConverter;
    private Units converter;
    private String[][] arrayUnits;
    private UnitsAdapter unitsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        Intent intent = getIntent();
        converterSetUp(intent.getIntExtra("converterNavId", 1000));

        resultOutput = (EditText) findViewById(R.id.resultOut);
        resultConverter = (EditText) findViewById(R.id.resultConverter);

        resultOutput.setOnFocusChangeListener(mResultOnClickListener);
        resultConverter.setOnFocusChangeListener(mResultOnClickListener);

        unitsAdapter = new UnitsAdapter(getApplicationContext(), arrayUnits);

        spinnerFromConverter = (Spinner) findViewById(R.id.spinnerFromConverter);
        spinnerToConverter = (Spinner) findViewById(R.id.spinnerToConverter);

        spinnerFromConverter.setAdapter(unitsAdapter);
        spinnerToConverter.setAdapter(unitsAdapter);

        spinnerFromConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerToConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerFromConverter.setSelection(converter.getDisplayFrom());
        spinnerToConverter.setSelection(converter.getDisplayTo());
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
        int fromId = (int) spinnerFromConverter.getSelectedItemId();
        int toId = (int) spinnerToConverter.getSelectedItemId();

        double result = converter.calculate(
                convertStringToDouble(resultOutput.getText().toString()),
                unitsAdapter.getItemName(fromId),
                unitsAdapter.getItemName(toId)
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
