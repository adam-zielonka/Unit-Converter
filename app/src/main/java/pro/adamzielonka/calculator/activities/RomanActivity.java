package pro.adamzielonka.calculator.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.abstractes.BaseActivity;
import pro.adamzielonka.calculator.calculators.RomanCalculator;

import static pro.adamzielonka.calculator.calculators.RomanCalculator.NAN_ROMAN;

public class RomanActivity extends BaseActivity {

    private EditText resultOutput;
    private TextView calculatorMemory;
    private RomanCalculator calculator;
    private boolean isPressedOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roman);

        setTitle(R.string.title_roman_calculator);
        mNavigationView.setCheckedItem(R.id.nav_roman_calculator);
        mItemId = R.id.nav_roman_calculator;

        calculator = new RomanCalculator();
        isPressedOperator = false;

        resultOutput = (EditText) findViewById(R.id.resultOutput);
        calculatorMemory = (TextView) findViewById(R.id.calculatorMemory);
    }

    public void onClickDigit(View v) {
        if (isPressedOperator) {
            resultOutput.setText("");
            isPressedOperator = false;
        }
        if (calculator.convertRomanToInt(resultOutput.getText().toString() + v.getTag().toString()) != NAN_ROMAN)
            resultOutput.append(v.getTag().toString());
    }

    public void onClickOperator(View v) {
        resultOutput.setText(calculator.calculate(resultOutput.getText().toString(), v.getTag().toString()));
        if (!calculator.getLastOperator().equals("="))
            calculatorMemory.setText(calculator.getMemory() + " " + calculator.getLastOperator());
        else
            calculatorMemory.setText("");
        isPressedOperator = true;
    }

    public void onClickSingleOperator(View v) {
        resultOutput.setText(calculator.singleCalculate(resultOutput.getText().toString(), v.getTag().toString()));
        if (!calculator.getLastOperator().equals("="))
            calculatorMemory.setText(calculator.getMemory() + " " + calculator.getLastOperator());
        else
            calculatorMemory.setText("");
    }

    public void onClickClear(View v) {
        resultOutput.setText("");
    }

    public void onClickClearAll(View v) {
        resultOutput.setText("");
        calculatorMemory.setText("");
        calculator.clear();
    }

    public void onClickDeleteLast(View v) {
        if (!isPressedOperator) {
            if (!resultOutput.getText().toString().isEmpty())
                resultOutput.setText(resultOutput.getText().toString().substring(0, resultOutput.getText().toString().length() - 1));
        } else {
            isPressedOperator = false;
            resultOutput.setText("");
        }
    }

}
