package pro.adamzielonka.converter.tools;

import java.util.Map;

import pro.adamzielonka.converter.models.concrete.ConcreteUnit;

import static pro.adamzielonka.converter.tools.Number.doubleToString;

public class Converter {
    public static double doConversion(double number, ConcreteUnit from, ConcreteUnit to) {
        return ((((number + from.shift1) * from.one) + from.shift2 - to.shift2) / to.one) - to.shift2;
    }

    public static String getFormula(Double one, Double shift1, Double shift2, String base) {
        String shift1F = shift1 != 0.0 ? (shift1 < 0.0) ? (" - " + doubleToString((-1) * shift1)) : (" + " + doubleToString(shift1)) : "";
        String shift2F = shift2 != 0.0 ? (shift2 < 0.0) ? (" - " + doubleToString((-1) * shift2)) : (" + " + doubleToString(shift2)) : "";
        String oneF = one != 1.0 ? doubleToString(one) + " * " : "";
        return shift1 != 0.0 && one != 1.0 ? oneF + "(" + base + shift1F + ")" + shift2F : oneF + base + shift1F + shift2F;
    }

    public static String getLanguageWords(Map<String, String> map, String langCode, String globalCode) {
        return map.containsKey(langCode) ? map.get(langCode) : (map.containsKey(globalCode) ? map.get(globalCode) : "");
    }

    public static String getLangCode() {
        return "en";
    }
}
