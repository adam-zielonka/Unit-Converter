package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnitsConverter {
    @SerializedName("units")
    @Expose
    private List<Unit> units = null;

    public List<Unit> getUnits() {
        return units;
    }

    private double getOne(String unitName) {
        for (Unit unit : units) {
            if (unit.getUnitName().equals(unitName)) {
                return unit.getOne();
            }
        }
        return 1.0;
    }

    private double getAdd(String unitName) {
        for (Unit unit : units) {
            if (unit.getUnitName().equals(unitName)) {
                return unit.getAdd();
            }
        }
        return 1.0;
    }

    public double convert(double number, String from, String to) {
        return (((number - getAdd(from)) * getOne(from)) / getOne(to)) + getAdd(to);
    }

}