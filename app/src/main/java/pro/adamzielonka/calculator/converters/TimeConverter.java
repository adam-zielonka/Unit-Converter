package pro.adamzielonka.calculator.converters;

import pro.adamzielonka.calculator.abstractes.Converter;

@SuppressWarnings("unused")
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
            case "ms":
                return 7;
            case "µs":
                return 8;
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
                    result *= 52.14285714285714;
                    if (to.equals("w")) break;
                case "w":
                    result *= 7.0;
                    if (to.equals("d")) break;
                case "d":
                    result *= 24.0;
                    if (to.equals("h")) break;
                case "h":
                    result *= 60.0;
                    if (to.equals("m")) break;
                case "m":
                    result *= 60.0;
                    if (to.equals("s")) break;
                case "s":
                    result *= 1000.0;
                    if (to.equals("ms")) break;
                case "ms":
                    result *= 1000.0;
                    if (to.equals("µs")) break;
                case "µs":

            }
        } else {
            switch (from) {
                case "µs":
                case "ms":
                    result /= 1000.0;
                    if (to.equals("ms")) break;
                case "s":
                    result /= 1000.0;
                    if (to.equals("s")) break;
                case "m":
                    result /= 60.0;
                    if (to.equals("m")) break;
                case "h":
                    result /= 60.0;
                    if (to.equals("h")) break;
                case "d":
                    result /= 24.0;
                    if (to.equals("d")) break;
                case "w":
                    result /= 7.0;
                    if (to.equals("w")) break;
                case "y":
                    result /= 52.14285714285714;
                    if (to.equals("y")) break;
            }
        }
        return result;
    }
}
