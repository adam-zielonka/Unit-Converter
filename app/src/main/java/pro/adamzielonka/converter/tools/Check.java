package pro.adamzielonka.converter.tools;

import java.util.List;

import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

public class Check {
    public static boolean checkSymbolPrefixExist(String newName, List<Prefix> prefixes) {
        for (Prefix prefix : prefixes) {
            if (prefix.getPrefixName().equals(newName)) return true;
        }
        return false;
    }

    public static boolean checkSymbolUnitExist(String newName, List<Unit> units) {
        for (Unit unit : units) {
            if (unit.getUnitName().equals(newName)) return true;
        }
        return false;
    }
}
