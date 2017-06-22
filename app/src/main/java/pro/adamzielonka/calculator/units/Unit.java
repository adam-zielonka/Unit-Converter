package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
class Unit {
    @SerializedName("unitName")
    @Expose
    private String unitName = "";
    @SerializedName("unitDescription")
    @Expose
    private String unitDescription = "";
    @SerializedName("prefixBase")
    @Expose
    private Double prefixBase = 10.0;
    @SerializedName("one")
    @Expose
    private Double one = 1.0;
    @SerializedName("shift")
    @Expose
    private Double shift = 0.0;
    @SerializedName("prefixes")
    @Expose
    private List<Prefix> prefixes = null;

    List<Prefix> getPrefixes() {
        return prefixes;
    }

    String getUnitName() {
        return unitName;
    }

    String getUnitDescription() {
        return unitDescription;
    }

    Double getOne() {
        return one;
    }

    Double getShift() {
        return shift;
    }

    Double getPrefixBase() {
        return prefixBase;
    }
}