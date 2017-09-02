package pro.adamzielonka.converter.tools;

import pro.adamzielonka.converter.models.concrete.CUnit;

import static pro.adamzielonka.java.Number.doubleToString;

public class Converter {
    public static double doConversion(double number, CUnit from, CUnit to) {
        return ((((number + from.shift1) * from.one) + from.shift2 - to.shift2) / to.one) - to.shift1;
    }

    public static String getFormula(Double one, Double shift1, Double shift2, String base) {
        String shift1F = shift1 != 0.0 ? (shift1 < 0.0) ? (" - " + doubleToString((-1) * shift1)) : (" + " + doubleToString(shift1)) : "";
        String shift2F = shift2 != 0.0 ? (shift2 < 0.0) ? (" - " + doubleToString((-1) * shift2)) : (" + " + doubleToString(shift2)) : "";
        String oneF = one != 1.0 ? doubleToString(one) + " * " : "";
        return shift1 != 0.0 && one != 1.0 ? oneF + "(" + base + shift1F + ")" + shift2F : oneF + base + shift1F + shift2F;
    }
}