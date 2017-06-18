package pro.adamzielonka.calculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.classes.ByteConverter;
import pro.adamzielonka.calculator.classes.IConverter;
import pro.adamzielonka.calculator.classes.TemperatureConverter;

public class ConverterActivity extends BaseActivity {

    private TextView resultOutput;
    private TextView resultConverter;
    private Spinner spinnerFromConverter;
    private Spinner spinnerToConverter;
    private IConverter converter;
    private int arrayItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        Intent intent = getIntent();

        switch (intent.getStringExtra("converterName")) {
            case "Temperature":
                converter = new TemperatureConverter();
                arrayItems = R.array.temperatureItems;
                setTitle(R.string.title_converter_temperature);
                break;
            case "Byte":
                converter = new ByteConverter();
                arrayItems = R.array.byteItmes;
                setTitle(R.string.title_converter_byte);
                break;
        }

        for (int i = 0; i < 10; i++) {
            setListenerToButton(mButtonClickDigitListener, "" + i);
        }

        setListenerToButton(mButtonClickClearOutputListener, "ClearOutput");
        setListenerToButton(mButtonClickComaListener, "Coma");
        setListenerToButton(mButtonClickSingleOperatorListener, "PlusMinus");
        setListenerToButton(mButtonClickDeleteLastListener, "DeleteLast");

        resultOutput = (TextView) findViewById(R.id.resultOutput);
        resultConverter = (TextView) findViewById(R.id.resultConverter);
        spinnerFromConverter = (Spinner) findViewById(R.id.spinnerFromConverter);
        spinnerToConverter = (Spinner) findViewById(R.id.spinnerToConverter);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, arrayItems, R.layout.spinner_layout);
        spinnerFromConverter.setAdapter(adapter);
        spinnerToConverter.setAdapter(adapter);
        spinnerFromConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerToConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerToConverter.setSelection(1);
    }

    private AdapterView.OnItemSelectedListener mSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            calculateAndPrintResult();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

    };

    private void calculateAndPrintResult() {
        double result;
        try {
            result = converter.calculate(Double.parseDouble(resultOutput.getText().toString()), spinnerFromConverter.getSelectedItem().toString(), spinnerToConverter.getSelectedItem().toString());
        } catch (Exception e) {
            result = converter.calculate(0, spinnerFromConverter.getSelectedItem().toString(), spinnerToConverter.getSelectedItem().toString());
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        resultConverter.setText(numberFormat.format(result).replaceAll("\\s+", "").replaceAll(",", "."));
    }

    private View.OnClickListener mButtonClickDigitListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (resultOutput.getText().toString().equals("0"))
                resultOutput.setText("");
            if (resultOutput.getText().toString().equals("-0"))
                resultOutput.setText("-");
            resultOutput.append(v.getTag().toString());
            calculateAndPrintResult();
        }
    };

    private View.OnClickListener mButtonClickComaListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!resultOutput.getText().toString().contains("."))
                resultOutput.append(".");
        }
    };

    private View.OnClickListener mButtonClickSingleOperatorListener = new View.OnClickListener() {
        public void onClick(View v) {
            double result;
            try {
                result = converter.singleCalculate(Double.parseDouble(resultOutput.getText().toString()), v.getTag().toString());
            } catch (Exception e) {
                result = converter.singleCalculate(0, v.getTag().toString());
            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            resultOutput.setText(numberFormat.format(result).replaceAll("\\s+", "").replaceAll(",", "."));
            calculateAndPrintResult();
        }
    };

    private View.OnClickListener mButtonClickClearOutputListener = new View.OnClickListener() {
        public void onClick(View v) {
            resultOutput.setText("0");
            resultConverter.setText("0");
            calculateAndPrintResult();
        }
    };

    private View.OnClickListener mButtonClickDeleteLastListener = new View.OnClickListener() {
        public void onClick(View v) {
            resultOutput.setText(resultOutput.getText().toString().substring(0, resultOutput.getText().toString().length() - 1));
            if (resultOutput.getText().toString().isEmpty())
                resultOutput.setText("0");
            calculateAndPrintResult();
        }
    };

}
