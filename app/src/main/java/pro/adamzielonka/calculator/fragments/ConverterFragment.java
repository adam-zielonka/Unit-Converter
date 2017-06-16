package pro.adamzielonka.calculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.classes.Converter;

import java.text.NumberFormat;


public class ConverterFragment extends MyFragment {

    private TextView resultOutput;
    private TextView resultConverter;
    private Spinner spinnerConverter;
    private Converter converter;

    public ConverterFragment() {

    }

    public static ConverterFragment newInstance() {
        return new ConverterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        converter = new Converter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        for(int i=0;i<10;i++) {
            setListenerToButton(view, mButtonClickDigitListener, ""+i);
        }

        setListenerToButton(view, mButtonClickClearOutputListener, "ClearOutput");
        setListenerToButton(view, mButtonClickComaListener, "Coma");
        setListenerToButton(view, mButtonClickSingleOperatorListener, "PlusMinus");
        setListenerToButton(view, mButtonClickDeleteLastListener, "DeleteLast");

        resultOutput = (TextView) view.findViewById(R.id.resultOutput);
        resultConverter = (TextView) view.findViewById(R.id.resultConverter);
        spinnerConverter = (Spinner) view.findViewById(R.id.spinnerConverter);
        spinnerConverter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                calculateAndPrintResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        return view;
    }

    private void calculateAndPrintResult()
    {
        double result;
        try {
            result = converter.calculate(Double.parseDouble(resultOutput.getText().toString()),spinnerConverter.getSelectedItem().toString());
        } catch (Exception e) {
            result = converter.calculate(0,spinnerConverter.getSelectedItem().toString());
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        resultConverter.setText(numberFormat.format(result).replaceAll("\\s+","").replaceAll(",","."));
    }

    private View.OnClickListener mButtonClickDigitListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(resultOutput.getText().toString().equals("0"))
                resultOutput.setText("");
            if(resultOutput.getText().toString().equals("-0"))
                resultOutput.setText("-");
            resultOutput.append(v.getTag().toString());
            calculateAndPrintResult();
        }
    };

    private View.OnClickListener mButtonClickComaListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(!resultOutput.getText().toString().contains("."))
                resultOutput.append(".");
        }
    };

    private View.OnClickListener mButtonClickSingleOperatorListener = new View.OnClickListener() {
        public void onClick(View v) {
            double result;
            try {
                result = converter.singleCalculate(Double.parseDouble(resultOutput.getText().toString()),v.getTag().toString());
            } catch (Exception e) {
                result = converter.singleCalculate(0,v.getTag().toString());
            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            resultOutput.setText(numberFormat.format(result).replaceAll("\\s+","").replaceAll(",","."));
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
            resultOutput.setText(resultOutput.getText().toString().substring(0,resultOutput.getText().toString().length() - 1));
            if(resultOutput.getText().toString().isEmpty())
                resultOutput.setText("0");
            calculateAndPrintResult();
        }
    };
}
