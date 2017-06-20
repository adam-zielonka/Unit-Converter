package pro.adamzielonka.calculator.calculators;

public class Calculator {
    private double result;
    private String lastOperator;

    public Calculator() {
        clear();
    }

    public void clear() {
        result = 0;
        lastOperator = "=";
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public double getMemory() {
        return result;
    }

    public double calculate(double number, String operator) {
        switch (lastOperator) {
            case "+":
                result = result + number;
                break;
            case "-":
                result = result - number;
                break;
            case "*":
                result = result * number;
                break;
            case "/":
                result = result / number;
                break;
            case "=":
            default:
                result = number;
                break;
        }
        lastOperator = operator;
        return result;
    }

    public double singleCalculate(double number, String operator) {
        switch (operator) {
            case "+-":
                return (-1) * number;
            default:
                return number;
        }
    }
}
