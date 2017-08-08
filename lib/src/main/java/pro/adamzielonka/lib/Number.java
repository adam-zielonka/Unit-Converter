package pro.adamzielonka.lib;

import java.text.DecimalFormat;

public class Number {
    private static final int MAX_DIGIT_COUNT = 15;
    private static final String SEPARATOR_DECIMAL = ".";
    private static final String NO_SEPARATOR_DECIMAL = ",";

    public static String doubleToString(Double number) {
        DecimalFormat exponentNotation = new DecimalFormat("0.#################E0");
        DecimalFormat numberFormat = new DecimalFormat("#.#################");

        String result = exponentNotation.format(number);
        int exponent = result.contains("E")
                ? Integer.parseInt(result.substring(result.indexOf("E") + 1, result.length())) : 0;

        return (Math.abs(exponent) >= MAX_DIGIT_COUNT ? result : numberFormat.format(number)).replaceAll(NO_SEPARATOR_DECIMAL, SEPARATOR_DECIMAL);
    }

    public static Double stringToDouble(String number) {
        try {
            return Double.parseDouble(number.replaceAll("\\s+", "").replaceAll(NO_SEPARATOR_DECIMAL, SEPARATOR_DECIMAL));
        } catch (NumberFormatException e) {
            switch (number) {
                case "":
                    return 0.0;
                case "∞":
                    return Double.POSITIVE_INFINITY;
                case "-∞":
                    return Double.NEGATIVE_INFINITY;
                default:
                    return Double.NaN;
            }
        }
    }

    private static int digitCount(String number) {
        return number.replace("-", "").replace(SEPARATOR_DECIMAL, "").length();
    }

    public static String appendDigit(String number, String digit) {
        if (number.contains("∞") || number.equals("NaN") || digitCount(number) >= MAX_DIGIT_COUNT)
            return number;

        return !number.equals("-0") ? !number.equals("0") ? number + digit : digit : "-" + digit;
    }

    public static String appendComma(String number) {
        if (number.contains("∞") || number.equals("NaN")) return number;
        return number.contains(SEPARATOR_DECIMAL) ? number : number + SEPARATOR_DECIMAL;
    }

    public static String changeSign(String number) {
        return number.equals("NaN") ? number : doubleToString((-1.0) * stringToDouble(number));
    }

    public static String deleteLast(String number) {
        if (number.contains("∞") || number.equals("NaN") || number.isEmpty()) return "0";
        String result = number.substring(0, number.length() - 1);
        return result.isEmpty() ? "0" : result;
    }
}
