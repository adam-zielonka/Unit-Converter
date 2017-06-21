package pro.adamzielonka.calculator.converters;

import pro.adamzielonka.calculator.abstractes.Converter;

@SuppressWarnings("unused")
public class LengthConverter extends Converter {

    private int letterToNumber(String letter) {
        switch (letter) {
            case "in":
                return 1;
            case "ft":
                return 2;
            case "yd":
                return 3;
            case "nmi":
                return 4;
            case "mi":
                return 5;
            case "km":
                return 6;
            case "m":
                return 7;
            case "dm":
                return 8;
            case "cm":
                return 9;
            case "mm":
                return 10;
            case "µm":
                return 11;
            case "nm":
                return 12;
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
                case "nm":
                case "µm":
                    result /= 1000.0;
                    if (to.equals("µm")) break;
                case "mm":
                    result /= 1000.0;
                    if (to.equals("mm")) break;
                case "cm":
                    result /= 10.0;
                    if (to.equals("cm")) break;
                case "dm":
                    result /= 10.0;
                    if (to.equals("dm")) break;
                case "m":
                    result /= 10.0;
                    if (to.equals("m")) break;
                case "km":
                    result *= 1000.0;
                    if (to.equals("km")) break;
                case "mi":
                    result *= 1/1.609344;
                    if (to.equals("mi")) break;
                case "nmi":
                    if (to.equals("nmi")) break;
                case "yd":
                    if (to.equals("yd")) break;
                case "ft":
                    if (to.equals("ft")) break;
                case "in":
                    if (to.equals("in")) break;

            }
        } else {
            switch (from) {
                case "in":
                    if (to.equals("in")) break;
                case "ft":
                    if (to.equals("ft")) break;
                case "yd":
                    if (to.equals("yd")) break;
                case "nmi":
                    if (to.equals("nmi")) break;
                case "mi":
                    if (to.equals("mi")) break;
                case "km":
                    if (to.equals("km")) break;
                case "m":
                    if (to.equals("m")) break;
                case "dm":
                    if (to.equals("dm")) break;
                case "cm":
                    if (to.equals("cm")) break;
                case "mm":
                    if (to.equals("mm")) break;
                case "µm":
                    if (to.equals("µm")) break;
                case "nm":
            }
        }
        return result;
    }
}
