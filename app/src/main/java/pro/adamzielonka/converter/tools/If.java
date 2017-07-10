package pro.adamzielonka.converter.tools;

import java.util.List;

import pro.adamzielonka.converter.units.user.Prefix;
import pro.adamzielonka.converter.units.user.Unit;

public class If {
    public static boolean isSymbolPrefixExist(String newName, List<Prefix> prefixes) {
        for (Prefix prefix : prefixes) {
            if (prefix.getSymbol().equals(newName)) return true;
        }
        return false;
    }

    public static boolean isSymbolUnitExist(String newName, List<Unit> units) {
        for (Unit unit : units) {
            if (unit.getSymbol().equals(newName)) return true;
        }
        return false;
    }
}
