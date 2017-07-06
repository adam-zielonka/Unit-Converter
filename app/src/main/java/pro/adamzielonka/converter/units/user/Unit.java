package pro.adamzielonka.converter.units.user;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal"})
public class Unit {
    private String unitName = "";
    private String unitDescriptionFirst = "";
    private String unitDescription = "";
    private Integer unitPosition = 0;
    private Double prefixBase = 10.0;
    private Double one = 1.0;
    private Double shift = 0.0;
    private Double shift2 = 0.0;
    private List<Prefix> prefixes = new ArrayList<>();

    public List<Prefix> getPrefixes() {
        return prefixes;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getUnitDescriptionFirst() {
        return unitDescriptionFirst;
    }

    public String getUnitDescription() {
        return unitDescription;
    }

    public Integer getUnitPosition() {
        return unitPosition;
    }

    public Double getOne() {
        return one;
    }

    public Double getShift() {
        return shift;
    }

    public Double getShift2() {
        return shift2;
    }

    public Double getPrefixBase() {
        return prefixBase;
    }
}