package pro.adamzielonka.calculator.calculators;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CalculatorTest {
    private double delta = 0.0000000001;

    @Test
    public void clear() throws Exception {
        Calculator calculator = new Calculator();
        calculator.calculate(2.0, "+");
        calculator.calculate(2.0, "=");
        calculator.clear();
        assertEquals(calculator.getLastOperator(), "=");
        assertEquals(calculator.getMemory(), 0.0, delta);
    }

    @Test
    public void getLastOperator() throws Exception {
        Calculator calculator = new Calculator();
        calculator.calculate(2.0, "+");
        assertEquals(calculator.getLastOperator(), "+");
        calculator.calculate(2.0, "=");
        assertEquals(calculator.getLastOperator(), "=");
    }

    @Test
    public void getMemory() throws Exception {
        Calculator calculator = new Calculator();
        calculator.calculate(2.0, "+");
        assertEquals(calculator.getMemory(), 2.0, delta);
        calculator.calculate(2.0, "=");
        assertEquals(calculator.getMemory(), 4.0, delta);
    }

    @Test
    public void calculateOperatorPlus() throws Exception {
        Calculator calculator = new Calculator();
        assertEquals(calculator.calculate(2.0, "+"), 2.0, delta);
        assertEquals(calculator.calculate(2.0, "="), 4.0, delta);
    }

    @Test
    public void calculateOperatorMinus() throws Exception {
        Calculator calculator = new Calculator();
        assertEquals(calculator.calculate(2.0, "-"), 2.0, delta);
        assertEquals(calculator.calculate(2.0, "="), 0.0, delta);
    }

    @Test
    public void calculateOperatorMultiple() throws Exception {
        Calculator calculator = new Calculator();
        assertEquals(calculator.calculate(2.0, "*"), 2.0, delta);
        assertEquals(calculator.calculate(2.0, "="), 4.0, delta);
    }

    @Test
    public void calculateOperatorDivide() throws Exception {
        Calculator calculator = new Calculator();
        assertEquals(calculator.calculate(2.0, "/"), 2.0, delta);
        assertEquals(calculator.calculate(2.0, "="), 1.0, delta);
    }

    @Test
    public void singleCalculate() throws Exception {
        Calculator calculator = new Calculator();
        assertEquals(calculator.singleCalculate(2.0, "+-"), -2.0, delta);
        assertEquals(calculator.singleCalculate(-2.0, "+-"), +2.0, delta);
    }

}