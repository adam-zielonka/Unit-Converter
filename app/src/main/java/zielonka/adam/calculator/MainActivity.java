package zielonka.adam.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private TextView resultOutput;
    private Calculator calculator;
    private boolean isPressedOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultOutput = (TextView) findViewById(R.id.resultOutput);
        calculator = new Calculator();
        isPressedOperator = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pressedDigit(View view) {
        if(isPressedOperator) {
            resultOutput.setText("");
            isPressedOperator = false;
        } else if(resultOutput.getText().toString().equals("0"))
            resultOutput.setText("");
        resultOutput.append(view.getTag().toString());
    }

    public void pressedComa(View view) {
        resultOutput.append(".");
    }

    public void pressedOperator(View view) {
        calculator.calculate(Double.parseDouble(resultOutput.getText().toString()),view.getTag().toString());
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        resultOutput.setText(numberFormat.format(calculator.getResult()).replaceAll("\\s+","").replaceAll(",","."));
        isPressedOperator = true;
    }

    public void pressedClearOutput(View view) {
        resultOutput.setText("0");
    }

    public void pressedClearAll(View view) {
        resultOutput.setText("0");
        calculator.clear();
    }
}
