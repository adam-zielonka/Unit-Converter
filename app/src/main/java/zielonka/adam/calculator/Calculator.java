package zielonka.adam.calculator;

class Calculator {
    private double lastNumber;
    private String lastOperator;

    Calculator() {
        lastNumber = 0;
        lastOperator = "=";
    }

    void clear() {
        lastNumber = 0;
        lastOperator = "=";
    }

    double getResult() {
        return lastNumber;
    }

    void calculate(double number, String operator) {
        switch (lastOperator) {
            case "+": lastNumber = lastNumber + number; break;
            case "-": lastNumber = lastNumber - number; break;
            case "*": lastNumber = lastNumber * number; break;
            case "/": lastNumber = lastNumber / number; break;
            case "=": lastNumber = number; break;
        }
        lastOperator = operator;
    }

    void singleCalculate(double number, String operator) {
        switch (operator) {
            case "+-": lastNumber = (-1) * number; break;
        }
        lastOperator = operator;
    }
}
