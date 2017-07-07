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

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setUnitDescriptionFirst(String unitDescriptionFirst) {
        this.unitDescriptionFirst = unitDescriptionFirst;
    }

    public void setUnitDescription(String unitDescription) {
        this.unitDescription = unitDescription;
    }

    public void setUnitPosition(Integer unitPosition) {
        this.unitPosition = unitPosition;
    }

    public void setPrefixBase(Double prefixBase) {
        this.prefixBase = prefixBase;
    }

    public void setOne(Double one) {
        this.one = one;
    }

    public void setShift(Double shift) {
        this.shift = shift;
    }

    public void setShift2(Double shift2) {
        this.shift2 = shift2;
    }

    public void setPrefixes(List<Prefix> prefixes) {
        this.prefixes = prefixes;
    }
}