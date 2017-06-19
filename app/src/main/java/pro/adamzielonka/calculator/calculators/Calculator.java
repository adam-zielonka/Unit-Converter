package pro.adamzielonka.calculator.calculators;

import java.text.NumberFormat;

public class Calculator {
    private double result;
    private String lastNumber;
    private String lastOperator;
    private String lastOperation;

    public Calculator() {
        clear();
    }

    public void clear() {
        result = 0;
        lastNumber = "";
        lastOperator = "=";
        lastOperation = "";
    }

    public String getResult() {
        if(prepareString(result).length() >= 15)
            return prepareNumber(prepareString(result)) + " ";
        return prepareString(result);
    }

    private String prepareString(double number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        return numberFormat.format(number).replaceAll("\\s+", "").replaceAll(",", ".");
    }

    public String getMemory() {
        if (!lastOperator.equals("="))
            lastOperation += lastNumber + " " + lastOperator + " ";
        else
            lastOperation = "";
        return lastOperation;
    }

    private double prepareNumber(String number) {
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return 0;
        }
    }

    public void calculate(String strNumber, String operator) {
        double number = prepareNumber(strNumber);
        lastNumber = strNumber;
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
                result = number;
                break;
        }
        lastOperator = operator;
    }

    public String singleCalculate(String srtNumber, String operator) {
        double number = prepareNumber(srtNumber);
        switch (operator) {
            case "+-":
                return prepareString((-1) * number);
            default:
                return srtNumber;
        }
    }
}
