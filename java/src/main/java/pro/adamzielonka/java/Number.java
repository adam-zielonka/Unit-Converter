package pro.adamzielonka.java;

import static pro.adamzielonka.java.NumberFunctions.MAX_DIGITS;
import static pro.adamzielonka.java.NumberFunctions.containsNaN;
import static pro.adamzielonka.java.NumberFunctions.digitCount;
import static pro.adamzielonka.java.NumberFunctions.formatNumber;
import static pro.adamzielonka.java.NumberFunctions.getNumberWithOutZero;
import static pro.adamzielonka.java.NumberFunctions.getNumberWithZero;
import static pro.adamzielonka.java.NumberFunctions.prepareStringIn;
import static pro.adamzielonka.java.NumberFunctions.prepareStringOut;
import static pro.adamzielonka.java.Symbols.EMPTY;
import static pro.adamzielonka.java.Symbols.INFINITY;
import static pro.adamzielonka.java.Symbols.MINUS;
import static pro.adamzielonka.java.Symbols.MINUS_INFINITY;
import static pro.adamzielonka.java.Symbols.ZERO;

public class Number {

    //region Parse
    public static String doubleToString(Double number) {
        return prepareStringOut(formatNumber(number));
    }

    public static Double stringToDouble(String number) {
        switch (number) {
            case EMPTY:
            case MINUS:
                return 0.0;
            case INFINITY:
                return Double.POSITIVE_INFINITY;
            case MINUS_INFINITY:
                return Double.NEGATIVE_INFINITY;
            default:
                try {
                    return Double.parseDouble(prepareStringIn(number));
                } catch (NumberFormatException e) {
                    return Double.NaN;
                }
        }
    }
    //endregion

    //region Edit number
    public static String appendDigit(String number, String digit) {
        if (containsNaN(number) || digitCount(number) >= MAX_DIGITS) return number;
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
        if (containsNaN(number) || number.length() <= 1) return ZERO;
        return number.substring(0, number.length() - 1);
    }
    //endregion

    //region Separator
    public static String getDecimalSeparator() {
        return Separator.getInstance().getDecimalSeparator();
    }

    public static void setCommaDecimalSeparator() {
        Separator.getInstance().setCommaDecimalSeparator();
    }

    public static void setDotDecimalSeparator() {
        Separator.getInstance().setDotDecimalSeparator();
    }
    //endregion
}
