package pro.adamzielonka.converter.tools;

import pro.adamzielonka.converter.units.concrete.ConcreteUnit;

public class UnitConverter {
    public static double doConversion(double number, ConcreteUnit from, ConcreteUnit to) {
        return ((((number + from.getShift1()) * from.getOne()) + from.getShift2() - to.getShift2()) / to.getOne()) - to.getShift1();
    }
}
