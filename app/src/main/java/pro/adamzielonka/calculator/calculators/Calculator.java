package pro.adamzielonka.calculator.calculators;

public class Calculator {
    private double lastNumber;
    private String lastOperator;

    public Calculator() {
        clear();
    }

    public void clear() {
        lastNumber = 0;
        lastOperator = "=";
    }

    public double getResult() {
        return lastNumber;
    }

    public void calculate(double number, String operator) {
        switch (lastOperator) {
            case "+": lastNumber = lastNumber + number; break;
            case "-": lastNumber = lastNumber - number; break;
            case "*": lastNumber = lastNumber * number; break;
            case "/": lastNumber = lastNumber / number; break;
            case "=": lastNumber = number; break;
        }
        lastOperator = operator;
    }

    public void singleCalculate(double number, String operator) {
        switch (operator) {
            case "+-": lastNumber = (-1) * number; break;
        }
        lastOperator = operator;
    }
}
