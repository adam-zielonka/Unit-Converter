package pro.adamzielonka.converter.units.user;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal"})
class Unit {
    private String unitName = "";
    private String unitDescriptionFirst = "";
    private String unitDescription = "";
    private Integer unitPosition = 0;
    private Double prefixBase = 10.0;
    private Double one = 1.0;
    private Double shift = 0.0;
    private Double shift2 = 0.0;
    private List<Prefix> prefixes = new ArrayList<>();

    List<Prefix> getPrefixes() {
        return prefixes;
    }

    String getUnitName() {
        return unitName;
    }

    String getUnitDescriptionFirst() {
        return unitDescriptionFirst;
    }

    String getUnitDescription() {
        return unitDescription;
    }

    Integer getUnitPosition() {
        return unitPosition;
    }

    Double getOne() {
        return one;
    }

    Double getShift() {
        return shift;
    }

    Double getShift2() {
        return shift2;
    }

    Double getPrefixBase() {
        return prefixBase;
    }
}