package pro.adamzielonka.calculator.calculators;

public class Calculator {
    private double result;
    private String lastOperator;
    private double lastDigit;

    public Calculator() {
        clear();
    }

    public void clear() {
        result = 0;
        lastDigit = 0;
        lastOperator = "=";
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public double getMemory() {
        return result;
    }

    public void setLastOperator(String lastOperator) {
        this.lastOperator = lastOperator;
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
        if (!operator.equals("="))
            lastOperator = operator;
        return result;
    }

    public double repeatCalculate(){
        return calculate(lastDigit,"=");
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
