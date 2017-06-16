package pro.adamzielonka.calculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.classes.IConverter;
import pro.adamzielonka.calculator.classes.TemperatureConverter;


public class ConverterFragment extends MyFragment {

    private TextView resultOutput;
    private TextView resultConverter;
    private Spinner spinnerFromConverter;
    private Spinner spinnerToConverter;
    private IConverter converter;
    private int arrayItems;

    public ConverterFragment() {
    }

    public static ConverterFragment newInstance(int arrayItems, String converterName) {
        ConverterFragment converterFragment = new ConverterFragment();

        Bundle args = new Bundle();
        args.putInt("arrayItems", arrayItems);
        args.putString("converterName", converterName);
        converterFragment.setArguments(args);

        return converterFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getArguments().getString("converterName", "Temperature")) {
            case "Temperature":
                converter = new TemperatureConverter();
        }
        arrayItems = getArguments().getInt("arrayItems");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        for (int i = 0; i < 10; i++) {
            setListenerToButton(view, mButtonClickDigitListener, "" + i);
        }

        setListenerToButton(view, mButtonClickClearOutputListener, "ClearOutput");
        setListenerToButton(view, mButtonClickComaListener, "Coma");
        setListenerToButton(view, mButtonClickSingleOperatorListener, "PlusMinus");
        setListenerToButton(view, mButtonClickDeleteLastListener, "DeleteLast");

        resultOutput = (TextView) view.findViewById(R.id.resultOutput);
        resultConverter = (TextView) view.findViewById(R.id.resultConverter);
        spinnerFromConverter = (Spinner) view.findViewById(R.id.spinnerFromConverter);
        spinnerToConverter = (Spinner) view.findViewById(R.id.spinnerToConverter);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this.getContext(), arrayItems, R.layout.spinner_layout);
        spinnerFromConverter.setAdapter(adapter);
        spinnerToConverter.setAdapter(adapter);
        spinnerFromConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerToConverter.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        spinnerToConverter.setSelection(1);
        return view;
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
