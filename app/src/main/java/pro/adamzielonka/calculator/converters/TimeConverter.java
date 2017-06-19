package pro.adamzielonka.calculator.converters;

import pro.adamzielonka.calculator.abstractes.Converter;

public class TimeConverter extends Converter {

    private int letterToNumber(String letter) {
        switch (letter) {
            case "y":
                return 1;
            case "w":
                return 2;
            case "d":
                return 3;
            case "h":
                return 4;
            case "m":
                return 5;
            case "s":
                return 6;
            default:
                return -1;
        }
    }

    @Override
    public double calculate(double number, String from, String to) {
        if (from.equals(to)) return number;

        int fromNumber = letterToNumber(from);
        int toNumber = letterToNumber(to);

        if (fromNumber == -1 || toNumber == -1)
            return number;

        double result = number;

        if (fromNumber < toNumber) {
            switch (from) {
                case "y":
                case "w":
                    result *= 52.14285714285714;
                    if (to.equals("w")) break;
                case "d":
                    result *= 7;
                    if (to.equals("d")) break;
                case "h":
                    result *= 24;
                    if (to.equals("d")) break;
                case "m":
                    result *= 60;
                    if (to.equals("d")) break;
                case "s":
                    result *= 60;
                    if (to.equals("d")) break;
            }
        } else {
            switch (from) {
                case "s":
                case "m":
                    result /= 60;
                    if (to.equals("m")) break;
                case "h":
                    result /= 60;
                    if (to.equals("h")) break;
                case "d":
                    result /= 24;
                    if (to.equals("d")) break;
                case "w":
                    result /= 7;
                    if (to.equals("w")) break;
                case "y":
                    result /= 52.14285714285714;
                    if (to.equals("y")) break;
            }
        }
        return result;
    }
}
