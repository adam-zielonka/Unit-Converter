package pro.adamzielonka.calculator.calculators;

public class NewCalculator {
    private boolean pressedEqual;
    private boolean pressedOperator;
    private double number;
    private double result;
    private String operator;


    public NewCalculator() {
        clear();
    }

    public void clear() {
        pressedEqual = true;
        pressedOperator = false;
        number = 0.0;
        result = 0.0;
        operator = "";
    }

    public boolean isPressedEqual() {
        return pressedEqual;
    }

    public boolean isPressedOperator() {
        return pressedOperator;
    }

    public NewCalculator setOperator(String operator) {
        this.operator = operator;
        pressedOperator = true;
        return this;
    }

    public NewCalculator setNumber(double number) {
        this.number = number;
        return this;
    }

    public NewCalculator setPressedEqual(boolean pressedEqual) {
        this.pressedEqual = pressedEqual;
        return this;
    }

    public NewCalculator setPressedOperator(boolean pressedOperator) {
        this.pressedOperator = pressedOperator;
        return this;
    }

    public double getResult() {
        return result;
    }

    public String getOperator() {
        return operator;
    }

    public double calculate() {
        switch (operator) {
            case "+":
                result += number;
                break;
            case "-":
                result -= number;
                break;
            case "*":
                result *= number;
                break;
            case "/":
                result /= number;
                break;
        }
        pressedEqual = false;
        return result;
    }
}
