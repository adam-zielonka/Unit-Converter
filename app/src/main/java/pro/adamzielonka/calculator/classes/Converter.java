package pro.adamzielonka.calculator.classes;

public class Converter {

    public Converter() {
    }

    public double calculate(double number, String operator) {
        switch (operator) {
            case "℃ ➙ ℉": return ((9.0/5.0)*number)+32.0;
            case "℉ ➙ ℃": return ((5.0/9.0)*(number-32.0));
            case "℃ ➙ K": return number + 273.15;
            case "℉ ➙ K": return ((5.0/9.0)*(number-32.0)) + 273.15;
            case "K ➙ ℃": return number - 273.15;
            case "K ➙ ℉": return ((9.0/5.0)*(number - 273.15))+32.0;
            default: return number;
        }
    }

    public double singleCalculate(double number, String operator) {
        switch (operator) {
            case "+-": return (-1) * number;
            default: return 0;
        }
    }
}
