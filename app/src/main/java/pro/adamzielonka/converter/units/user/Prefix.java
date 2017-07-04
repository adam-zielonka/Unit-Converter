package pro.adamzielonka.converter.units.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
class Prefix {
    @SerializedName("prefixName")
    @Expose
    private String prefixName = "";
    @SerializedName("prefixDescription")
    @Expose
    private String prefixDescription = "";
    @SerializedName("prefixExponent")
    @Expose
    private Double prefixExponent = 1.0;
    @SerializedName("unitPosition")
    @Expose
    private Integer unitPosition = 0;

    String getPrefixName() {
        return prefixName;
    }

    String getPrefixDescription() {
        return prefixDescription;
    }

    Integer getUnitPosition() {
        return unitPosition;
    }

    Double getPrefixExponent() {
        return prefixExponent;
    }
}
