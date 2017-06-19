package pro.adamzielonka.calculator.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.text.NumberFormat;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.abstractes.BaseActivity;
import pro.adamzielonka.calculator.calculators.Calculator;

public class CalculatorActivity extends BaseActivity {

    private EditText resultOutput;
    private Calculator calculator;
    private boolean isPressedOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        setTitle(R.string.title_calculator);

        calculator = new Calculator();
        isPressedOperator = false;

        resultOutput = (EditText) findViewById(R.id.resultOutput);
    }

    public void onClickDigit(View v) {
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

    public void onClickSingleOperator(View v) {
        try {
            calculator.singleCalculate(Double.parseDouble(resultOutput.getText().toString()), v.getTag().toString());
        } catch (Exception e) {
            calculator.singleCalculate(0, v.getTag().toString());
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        resultOutput.setText(numberFormat.format(calculator.getResult()).replaceAll("\\s+", "").replaceAll(",", "."));
    }

    public void onClickClear(View v) {
        resultOutput.setText("0");
    }

    public void onClickClearAll(View v) {
        resultOutput.setText("0");
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
