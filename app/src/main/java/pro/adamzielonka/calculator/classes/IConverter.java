package pro.adamzielonka.calculator.classes;

public interface IConverter {
    double calculate(double number, String from, String to);
    double singleCalculate(double number, String operator);
}
