package zielonka.adam.calculator;

public class Calculator {
    private double lastNumber;
    private String lastOprator;

    public Calculator() {
        lastNumber = 0;
        lastOprator = "=";
    }

    public void clear() {
        lastNumber = 0;
        lastOprator = "=";
    }

    public double getResult() {
        return lastNumber;
    }

    public void calculate(double number, String operator) {
        switch (lastOprator) {
            case "+": lastNumber = lastNumber + number; break;
            case "-": lastNumber = lastNumber - number; break;
            case "*": lastNumber = lastNumber * number; break;
            case "/": lastNumber = lastNumber / number; break;
            default: lastNumber = number; break;
        }
        lastOprator = operator;
    }
}
