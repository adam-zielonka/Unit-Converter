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
        int maxDigitCount = 15;
        if(resultOutput.getText().length() >= maxDigitCount) return;
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

    public void onClickComa(View v) {
        if (isPressedOperator) {
            resultOutput.setText("0.");
            isPressedOperator = false;
        } else if (!resultOutput.getText().toString().contains("."))
            resultOutput.append(".");
    }

    public void onClickOperator(View v) {
        calculator.calculate(resultOutput.getText().toString(), v.getTag().toString());
        resultOutput.setText(calculator.getResult());
        calculatorMemory.setText(calculator.getMemory());
        isPressedOperator = true;
    }

    public void onClickSingleOperator(View v) {
        resultOutput.setText(calculator.singleCalculate(resultOutput.getText().toString(), v.getTag().toString()));
        calculatorMemory.setText(calculator.getMemory());
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
    }

}
