package pro.adamzielonka.java;

import java.text.DecimalFormat;

import static pro.adamzielonka.java.Number.getDecimalSeparator;
import static pro.adamzielonka.java.Symbols.COMMA;
import static pro.adamzielonka.java.Symbols.DOT;
import static pro.adamzielonka.java.Symbols.EMPTY;
import static pro.adamzielonka.java.Symbols.EXP;
import static pro.adamzielonka.java.Symbols.INFINITY;
import static pro.adamzielonka.java.Symbols.MINUS;
import static pro.adamzielonka.java.Symbols.MINUS_INFINITY;
import static pro.adamzielonka.java.Symbols.MINUS_ZERO;
import static pro.adamzielonka.java.Symbols.NaN;
import static pro.adamzielonka.java.Symbols.ZERO;

class NumberFunctions {

    static final int MAX_DIGITS = 15;
    private static final String EXPONENT_NOTATION = "0.#################E0";
    private static final String STANDARD_NOTATION = "#.#################";

    //region Parse
    private static String formatNumber(Double number, String notation) {
        return new DecimalFormat(notation).format(number);
    }

    static String formatNumber(Double number) {
        return formatNumber(number, getNotation(number));
    }

    private static String getExponent(String number) {
        if (number.contains(EXP))
            return number.substring(number.indexOf(EXP) + 1, number.length());
        else return ZERO;
    }

    private static int getExponent(Double number) {
        return Integer.parseInt(getExponent(formatNumber(number, EXPONENT_NOTATION)));
    }

    private static boolean isNeedExponent(Double number) {
        return Math.abs(getExponent(number)) >= MAX_DIGITS;
    }

    private static String getNotation(Double number) {
        return isNeedExponent(number) ? EXPONENT_NOTATION : STANDARD_NOTATION;
    }

    static String prepareStringIn(String number) {
        return number.replaceAll("\\s+", EMPTY).replaceAll(COMMA, DOT);
    }

    static String prepareStringOut(String number) {
        return number.replaceAll(COMMA, getDecimalSeparator());
    }

    static Double returnNaN(String number) {
        switch (number) {
            case EMPTY:
            case MINUS:
                return 0.0;
            case INFINITY:
                return Double.POSITIVE_INFINITY;
            case MINUS_INFINITY:
                return Double.NEGATIVE_INFINITY;
            default:
                return Double.NaN;
        }
    }
    //endregion

    //region Edit number
    static int digitCount(String number) {
        return number.replace(MINUS, EMPTY).replace(getDecimalSeparator(), EMPTY).length();
    }

    static boolean containsNaN(String number) {
        return number.contains(INFINITY) || number.equals(NaN);
    }

    static String getNumberWithZero(String number) {
        return number.isEmpty() ? ZERO : number.equals(MINUS) ? MINUS_ZERO : number;
    }

    static String getNumberWithOutZero(String number) {
        return number.equals(ZERO) ? EMPTY : number.equals(MINUS_ZERO) ? MINUS : number;
    }
    //endregion
}
