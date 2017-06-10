package zielonka.adam.calculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;

import static zielonka.adam.calculator.TabbedActivity.PACKAGE_NAME;


public class CalculatorFragment extends Fragment {

    private TextView resultOutput;
    private Calculator calculator;
    private boolean isPressedOperator;

    public CalculatorFragment() {

    }

    static CalculatorFragment newInstance() {
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

        resultOutput = (TextView) view.findViewById(R.id.resultOutput);
        return view;
    }

    private void setListenerToButton(View view, View.OnClickListener mButtonClickListener, String buttonName) {
        Button button = (Button) view.findViewById(getIdResourceByName("button"+buttonName));
        button.setOnClickListener(mButtonClickListener);
    }

    private int getIdResourceByName(String aString) {
        return getResources().getIdentifier(aString, "id", PACKAGE_NAME);
    }

    private View.OnClickListener mButtonClickDigitListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(isPressedOperator) {
                resultOutput.setText("");
                isPressedOperator = false;
            } else if(resultOutput.getText().toString().equals("0"))
                resultOutput.setText("");
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
}
