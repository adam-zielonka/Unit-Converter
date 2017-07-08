package pro.adamzielonka.converter.tools;

import pro.adamzielonka.converter.units.concrete.ConcreteUnit;

import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class Converter {
    public static double doConversion(double number, ConcreteUnit from, ConcreteUnit to) {
        return ((((number + from.getShift1()) * from.getOne()) + from.getShift2() - to.getShift2()) / to.getOne()) - to.getShift1();
    }

    public static String getFormula(Double one, Double shift1, Double shift2, String base) {
        String shift1F = shift1 != 0.0 ? (shift1 < 0.0) ? (" - " + doubleToString((-1) * shift1)) : (" + " + doubleToString(shift1)) : "";
        String shift2F = shift2 != 0.0 ? (shift2 < 0.0) ? (" - " + doubleToString((-1) * shift2)) : (" + " + doubleToString(shift2)) : "";
        String oneF = one != 1.0 ? doubleToString(one) + " * " : "";
        return shift1 != 0.0 && one != 1.0 ? oneF + "(" + base + shift1F + ")" + shift2F : oneF + base + shift1F + shift2F;
    }
}
