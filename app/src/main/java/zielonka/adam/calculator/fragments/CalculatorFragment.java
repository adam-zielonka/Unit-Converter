package zielonka.adam.calculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import zielonka.adam.calculator.R;
import zielonka.adam.calculator.classes.Calculator;

import java.text.NumberFormat;


public class CalculatorFragment extends MyFragment {

    private TextView resultOutput;
    public Calculator calculator;
    private boolean isPressedOperator;

    public CalculatorFragment() {

    }

    public static CalculatorFragment newInstance() {
        return new CalculatorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calculator = new Calculator();
        isPressedOperator = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        for(int i=0;i<10;i++) {
            setListenerToButton(view, mButtonClickDigitListener, ""+i);
        }

        String operators[] = {"Plus","Minus","Multiple","Divide","Result"};

        for(String operator : operators) {
            setListenerToButton(view, mButtonClickOperatorListener, operator);
        }

        setListenerToButton(view, mButtonClickClearAllListener, "ClearAll");
        setListenerToButton(view, mButtonClickClearOutputListener, "ClearOutput");
        setListenerToButton(view, mButtonClickComaListener, "Coma");
        setListenerToButton(view, mButtonClickSingleOperatorListener, "PlusMinus");
        setListenerToButton(view, mButtonClickDeleteLastListener, "DeleteLast");

        resultOutput = (TextView) view.findViewById(R.id.resultOutput);
        return view;
    }

    private View.OnClickListener mButtonClickDigitListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(isPressedOperator) {
                resultOutput.setText("");
                isPressedOperator = false;
            }
            if(resultOutput.getText().toString().equals("0"))
                resultOutput.setText("");
            if(resultOutput.getText().toString().equals("-0"))
                resultOutput.setText("-");
            resultOutput.append(v.getTag().toString());
        }
    };

    private View.OnClickListener mButtonClickComaListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(isPressedOperator) {
                resultOutput.setText("0.");
                isPressedOperator = false;
            } else if(!resultOutput.getText().toString().contains("."))
                resultOutput.append(".");
        }
    };

    private View.OnClickListener mButtonClickOperatorListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                calculator.calculate(Double.parseDouble(resultOutput.getText().toString()),v.getTag().toString());
            } catch (Exception e) {
                calculator.calculate(0,v.getTag().toString());
                calculator.clear();
            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            resultOutput.setText(numberFormat.format(calculator.getResult()).replaceAll("\\s+","").replaceAll(",","."));
            isPressedOperator = true;
        }
    };

    private View.OnClickListener mButtonClickSingleOperatorListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                calculator.singleCalculate(Double.parseDouble(resultOutput.getText().toString()),v.getTag().toString());
            } catch (Exception e) {
                calculator.singleCalculate(0,v.getTag().toString());
            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            resultOutput.setText(numberFormat.format(calculator.getResult()).replaceAll("\\s+","").replaceAll(",","."));
        }
    };

    private View.OnClickListener mButtonClickClearOutputListener = new View.OnClickListener() {
        public void onClick(View v) {
            resultOutput.setText("0");
        }
    };

    private View.OnClickListener mButtonClickClearAllListener = new View.OnClickListener() {
        public void onClick(View v) {
            resultOutput.setText("0");
            calculator.clear();
        }
    };

    private View.OnClickListener mButtonClickDeleteLastListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!isPressedOperator) {
                resultOutput.setText(resultOutput.getText().toString().substring(0,resultOutput.getText().toString().length() - 1));
                if(resultOutput.getText().toString().isEmpty())
                    resultOutput.setText("0");
            } else {
                isPressedOperator = false;
                resultOutput.setText("0");
            }
        }
    };
}
