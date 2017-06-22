package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"FieldCanBeLocal", "CanBeFinal"})
class Prefix {
    @SerializedName("prefixName")
    @Expose
    private String prefixName = "";
    @SerializedName("prefixExponent")
    @Expose
    private Double prefixExponent = 1.0;

    String getPrefixName() {
        return prefixName;
    }

    Double getPrefixExponent() {
        return prefixExponent;
    }
}
