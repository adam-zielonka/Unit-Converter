package pro.adamzielonka.lib;

import java.text.DecimalFormat;

public class Number {

    private static final int MAX_DIGIT_COUNT = 15;
    private static final String INFINITY = "\u221E";

    public static String doubleToString(Double number) {
        DecimalFormat exponentNotation = new DecimalFormat("0.#################E0");
        DecimalFormat numberFormat = new DecimalFormat("#.#################");

        String result = exponentNotation.format(number);
        int exponent = result.contains("E")
                ? Integer.parseInt(result.substring(result.indexOf("E") + 1, result.length())) : 0;

        return (Math.abs(exponent) >= MAX_DIGIT_COUNT ? result : numberFormat.format(number)).replaceAll(",", getDecimalSeparator());
    }

    public static Double stringToDouble(String number) {
        try {
            return Double.parseDouble(number.replaceAll("\\s+", "").replaceAll(",", "."));
        } catch (NumberFormatException e) {
            switch (number) {
                case "":
                case "-":
                    return 0.0;
                case INFINITY:
                    return Double.POSITIVE_INFINITY;
                case "-" + INFINITY:
                    return Double.NEGATIVE_INFINITY;
                default:
                    return Double.NaN;
            }
        }
    }

    private static int digitCount(String number) {
        return number.replace("-", "").replace(getDecimalSeparator(), "").length();
    }

    private static boolean containsNaN(String number) {
        return number.contains(INFINITY) || number.equals("NaN");
    }

    private static String getNumberWithZero(String number) {
        return number.isEmpty() ? "0" : number.equals("-") ? "-0" : number;
    }

    private static String getNumberWithOutZero(String number) {
        return number.equals("0") ? "" : number.equals("-0") ? "-" : number;
    }

    public static String appendDigit(String number, String digit) {
        if (containsNaN(number) || digitCount(number) >= MAX_DIGIT_COUNT) return number;
        return getNumberWithOutZero(number) + digit;
    }

    public static String appendComma(String number) {
        if (containsNaN(number) || number.contains(getDecimalSeparator())) return number;
        return getNumberWithZero(number) + getDecimalSeparator();
    }

    public static String changeSign(String number) {
        return doubleToString(-1.0 * stringToDouble(number));
    }

    public static String deleteLast(String number) {
        if (containsNaN(number) || number.length() <= 1) return "0";
        return number.substring(0, number.length() - 1);
    }

    private static class Separator {
        private static final Separator ourInstance = new Separator();
        private String decimalSeparator = ".";

        static Separator getInstance() {
            return ourInstance;
        }

        private Separator() {
        }

        void setCommaDecimalSeparator(){
            decimalSeparator = ",";
        }

        void setDotDecimalSeparator(){
            decimalSeparator = ".";
        }

        String getDecimalSeparator() {
            return decimalSeparator;
        }
    }

    public static String getDecimalSeparator() {
        return Separator.getInstance().getDecimalSeparator();
    }

    public static void setCommaDecimalSeparator(){
        Separator.getInstance().setCommaDecimalSeparator();
    }

    public static void setDotDecimalSeparator(){
        Separator.getInstance().setDotDecimalSeparator();
    }
}
