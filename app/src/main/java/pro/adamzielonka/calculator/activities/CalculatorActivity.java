package pro.adamzielonka.calculator.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.abstractes.BaseActivity;
import pro.adamzielonka.calculator.calculators.Calculator;

public class CalculatorActivity extends BaseActivity {

    private EditText resultOutput;
    private TextView calculatorMemory;
    private Calculator calculator;
    private boolean isPressedOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        setTitle(R.string.title_calculator);
        mNavigationView.setCheckedItem(R.id.nav_calculator);
        mItemId = R.id.nav_calculator;

        calculator = new Calculator();
        isPressedOperator = false;

        resultOutput = (EditText) findViewById(R.id.resultOutput);
        calculatorMemory = (TextView) findViewById(R.id.calculatorMemory);
    }

    public void onClickDigit(View v) {
        if (isPressedOperator) {
            resultOutput.setText("");
            isPressedOperator = false;
        }
        int maxDigitCount = 15;
        if (resultOutput.getText().length() >= maxDigitCount) return;
        if (resultOutput.getText().toString().equals("0"))
            resultOutput.setText("");
        if (resultOutput.getText().toString().equals("-0"))
            resultOutput.setText("-");
        resultOutput.append(v.getTag().toString());
        if (!resultOutput.getText().toString().contains(","))
            resultOutput.setText(prepareString(resultOutput.getText().toString()));
    }

    public void onClickComa(View v) {
        if (isPressedOperator) {
            resultOutput.setText("0,");
            isPressedOperator = false;
        } else if (!resultOutput.getText().toString().contains(","))
            resultOutput.append(",");
    }

    public void onClickOperator(View v) {
        double result = calculator.calculate(
                convertStringToDouble(resultOutput.getText().toString()),
                v.getTag().toString()
        );
        resultOutput.setText(convertDoubleToString(result));
        if (!calculator.getLastOperator().equals("="))
            calculatorMemory.setText(convertDoubleToString(calculator.getMemory()) + " " + calculator.getLastOperator());
        else
            calculatorMemory.setText("");
        isPressedOperator = true;
    }

    public void onClickSingleOperator(View v) {
        double result = calculator.singleCalculate(
                convertStringToDouble(resultOutput.getText().toString()),
                v.getTag().toString()
        );
        resultOutput.setText(convertDoubleToString(result));
        if (!calculator.getLastOperator().equals("="))
            calculatorMemory.setText(convertDoubleToString(calculator.getMemory()) + " " + calculator.getLastOperator());
    }

    public void onClickClear(View v) {
        resultOutput.setText("0");
    }

    public void onClickClearAll(View v) {
        resultOutput.setText("0");
        calculatorMemory.setText("");
        calculator.clear();
    }

    public void onClickDeleteLast(View v) {
        if (!isPressedOperator) {
            resultOutput.setText(resultOutput.getText().toString().substring(0, resultOutput.getText().toString().length() - 1));
            if (resultOutput.getText().toString().isEmpty())
                resultOutput.setText("0");
        } else {
            isPressedOperator = false;
            resultOutput.setText("0");
        }
        resultOutput.setText(prepareString(resultOutput.getText().toString()));
    }

}
