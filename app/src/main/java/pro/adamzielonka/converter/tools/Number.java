package pro.adamzielonka.converter.tools;

import java.text.DecimalFormat;

public class Number {
    private static final int MAX_DIGIT_COUNT = 15;

    public static String doubleToString(Double number) {
        DecimalFormat exponentNotation = new DecimalFormat("0.#################E0");
        DecimalFormat numberFormat = new DecimalFormat("#.#################");

        String result = exponentNotation.format(number);
        int exponent = result.contains("E")
                ? Integer.parseInt(result.substring(result.indexOf("E") + 1, result.length())) : 0;

        return Math.abs(exponent) >= MAX_DIGIT_COUNT ? result : numberFormat.format(number);
    }

    public static Double stringToDouble(String number) {
        try {
            return Double.parseDouble(number.replaceAll("\\s+", "").replaceAll(",", "."));
        } catch (NumberFormatException e) {
            switch (number) {
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
        return number.replace("-", "").replace(",", "").length();
    }

    public static String appendDigit(String number, String digit) {
        if (number.contains("∞") || number.equals("NaN") || digitCount(number) >= MAX_DIGIT_COUNT)
            return number;

        return !number.equals("-0") ? !number.equals("0") ? number + digit : digit : "-" + digit;
    }

    public static String appendComa(String number) {
        return number.contains(",") ? number : number + ",";
    }

    public static String changeSign(String number) {
        return doubleToString((-1.0) * stringToDouble(number));
    }

    public static String deleteLast(String number) {
        String result = number.substring(0, number.length() - 1);
        return result.isEmpty() ? "0" : result;
    }
}
