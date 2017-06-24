package pro.adamzielonka.calculator.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.calculators.NewCalculator;

public class CalculatorActivity extends BaseActivity {

    private EditText resultOut;
    private EditText memoryOut;
    private NewCalculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        setTitle(R.string.title_basic_calculator);
        mNavigationView.setCheckedItem(R.id.nav_calculator);
        mItemId = R.id.nav_calculator;

        calculator = new NewCalculator();

        resultOut = (EditText) findViewById(R.id.resultOut);
        memoryOut = (EditText) findViewById(R.id.memoryOut);
    }

    public void onClickDigit(View v) {
        if (calculator.isPressedOperator()) {
            resultOut.setText("");
            calculator.setPressedOperator(false);
        }
        int maxDigitCount = 15;
        if (resultOut.getText().length() >= maxDigitCount) return;
        if (resultOut.getText().toString().equals("0"))
            resultOut.setText("");
        if (resultOut.getText().toString().equals("-0"))
            resultOut.setText("-");
        resultOut.append(v.getTag().toString());
        if (!resultOut.getText().toString().contains(","))
            resultOut.setText(prepareString(resultOut.getText().toString()));
    }

    public void onClickComa(View v) {
        if (calculator.isPressedOperator()) {
            resultOut.setText("0,");
            calculator.setPressedOperator(false);
        } else if (!resultOut.getText().toString().contains(","))
            resultOut.append(",");
    }

    public void onClickDeleteLast(View v) {
        if (!calculator.isPressedOperator()) {
            resultOut.setText(resultOut.getText().toString().substring(0, resultOut.getText().toString().length() - 1));
            if (resultOut.getText().toString().isEmpty())
                resultOut.setText("0");
        } else {
            calculator.setPressedOperator(false);
            resultOut.setText("0");
        }
        resultOut.setText(prepareString(resultOut.getText().toString()));
    }

    public void onClickClear(View v) {
        resultOut.setText("0");
    }

    public void onClickClearAll(View v) {
        resultOut.setText("0");
        memoryOut.setText("");
        calculator.clear();
    }

    public void onClickChangeSign(View v) {
        double result = (-1) * convertStringToDouble(resultOut.getText().toString());
        calculator.setNumber(result);
        resultOut.setText(convertDoubleToString(result));
    }

    public void printResult() {
        resultOut.setText(convertDoubleToString(calculator.getResult()));
        if (!calculator.isPressedEqual())
            memoryOut.setText(convertDoubleToString(calculator.getResult()) + " " + calculator.getOperator());
        else
            memoryOut.setText("");
    }

    public void onClickOperator(View v) {
        if (calculator.isPressedOperator()) {
            calculator.setOperator(v.getTag().toString());
        } else if (calculator.isPressedEqual()) {
            calculator.setOperator(v.getTag().toString())
                    .setNumber(convertStringToDouble(resultOut.getText().toString()));
        } else {
            calculator.setOperator(v.getTag().toString())
                    .setNumber(convertStringToDouble(resultOut.getText().toString()))
                    .calculate();
        }
        printResult();
    }

    public void onClickEqual(View v) {
        calculator.setPressedEqual(true)
                .calculate();
        printResult();
    }
}
