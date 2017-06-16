package pro.adamzielonka.calculator.classes;

public abstract class Converter implements IConverter {
    public double singleCalculate(double number, String operator) {
        switch (operator) {
            case "+-": return (-1) * number;
            default: return 0;
        }
    }
}
