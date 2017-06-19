package pro.adamzielonka.calculator.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.abstractes.BaseActivity;
import pro.adamzielonka.calculator.calculators.Calculator;

public class CalculatorActivity extends BaseActivity {

    private TextView resultOutput;
    private Calculator calculator;
    private boolean isPressedOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        setTitle(R.string.title_calculator);

        calculator = new Calculator();
        isPressedOperator = false;

        for (int i = 0; i < 10; i++) {
            setListenerToButton(mButtonClickDigitListener, "" + i);
        }

        String operators[] = {"Plus", "Minus", "Multiple", "Divide", "Result"};

        for (String operator : operators) {
            setListenerToButton(mButtonClickOperatorListener, operator);
        }

        setListenerToButton(mButtonClickClearAllListener, "ClearAll");
        setListenerToButton(mButtonClickClearOutputListener, "ClearOutput");
        setListenerToButton(mButtonClickComaListener, "Coma");
        setListenerToButton(mButtonClickSingleOperatorListener, "PlusMinus");
        setListenerToButton(mButtonClickDeleteLastListener, "DeleteLast");

        resultOutput = (TextView) findViewById(R.id.resultOutput);
    }

    private final View.OnClickListener mButtonClickDigitListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (isPressedOperator) {
                resultOutput.setText("");
                isPressedOperator = false;
            }
            if (resultOutput.getText().toString().equals("0"))
                resultOutput.setText("");
            if (resultOutput.getText().toString().equals("-0"))
                resultOutput.setText("-");
            resultOutput.append(v.getTag().toString());
        }
    };

    private final View.OnClickListener mButtonClickComaListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (isPressedOperator) {
                resultOutput.setText("0.");
                isPressedOperator = false;
            } else if (!resultOutput.getText().toString().contains("."))
                resultOutput.append(".");
        }
    };

    private final View.OnClickListener mButtonClickOperatorListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                calculator.calculate(Double.parseDouble(resultOutput.getText().toString()), v.getTag().toString());
            } catch (Exception e) {
                calculator.calculate(0, v.getTag().toString());
                calculator.clear();
            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            resultOutput.setText(numberFormat.format(calculator.getResult()).replaceAll("\\s+", "").replaceAll(",", "."));
            isPressedOperator = true;
        }
    };

    private final View.OnClickListener mButtonClickSingleOperatorListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                calculator.singleCalculate(Double.parseDouble(resultOutput.getText().toString()), v.getTag().toString());
            } catch (Exception e) {
                calculator.singleCalculate(0, v.getTag().toString());
            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            resultOutput.setText(numberFormat.format(calculator.getResult()).replaceAll("\\s+", "").replaceAll(",", "."));
        }
    };

    private final View.OnClickListener mButtonClickClearOutputListener = new View.OnClickListener() {
        public void onClick(View v) {
            resultOutput.setText("0");
        }
    };

    private final View.OnClickListener mButtonClickClearAllListener = new View.OnClickListener() {
        public void onClick(View v) {
            resultOutput.setText("0");
            calculator.clear();
        }
    };

    private final View.OnClickListener mButtonClickDeleteLastListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!isPressedOperator) {
                resultOutput.setText(resultOutput.getText().toString().substring(0, resultOutput.getText().toString().length() - 1));
                if (resultOutput.getText().toString().isEmpty())
                    resultOutput.setText("0");
            } else {
                isPressedOperator = false;
                resultOutput.setText("0");
            }
        }
    };

}
