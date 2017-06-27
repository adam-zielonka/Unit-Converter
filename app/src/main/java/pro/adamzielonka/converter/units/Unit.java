package pro.adamzielonka.converter.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
class Unit {
    @SerializedName("unitName")
    @Expose
    private String unitName = "";
    @SerializedName("unitDescriptionFirst")
    @Expose
    private String unitDescriptionFirst = "";
    @SerializedName("unitDescription")
    @Expose
    private String unitDescription = "";
    @SerializedName("unitPosition")
    @Expose
    private Integer unitPosition = 0;
    @SerializedName("prefixBase")
    @Expose
    private Double prefixBase = 10.0;
    @SerializedName("one")
    @Expose
    private Double one = 1.0;
    @SerializedName("shift")
    @Expose
    private Double shift = 0.0;
    @SerializedName("shift2")
    @Expose
    private Double shift2 = 0.0;
    @SerializedName("prefixes")
    @Expose
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