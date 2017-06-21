package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Unit {

    @SerializedName("unitName")
    @Expose
    private String unitName = "";
    @SerializedName("one")
    @Expose
    private Double one = 1.0;
    @SerializedName("add")
    @Expose
    private Double add = 0.0;

    String getUnitName() {
        return unitName;
    }

    Double getOne() {
        return one;
    }

    Double getAdd() {
        return add;
    }

}