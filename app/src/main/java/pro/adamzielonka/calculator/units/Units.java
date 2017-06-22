package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal"})
public class Units {
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("units")
    @Expose
    private List<Unit> units = null;

    public String getName() {
        return name;
    }

    private double getOne(String unitName) {
        for (Unit unit : units) {
            if (unit.getUnitName().equals(unitName)) {
                return unit.getOne();
            }
            if (unit.getPrefixes() == null) continue;
            for (Prefix prefix : unit.getPrefixes()) {
                if (unitName.equals(prefix.getPrefixName() + unit.getUnitName())) {
                    return unit.getOne() * Math.pow(unit.getPrefixBase(), prefix.getPrefixExponent());
                }
            }
        }
        return 1.0;
    }

    private double getShift(String unitName) {
        for (Unit unit : units) {
            if (unit.getUnitName().equals(unitName)) {
                return unit.getShift();
            }
        }
        return 0.0;
    }

    private int getCount() {
        int result = 0;
        for (Unit unit : units) {
            result++;
            if (unit.getPrefixes() == null) continue;
            for (Prefix prefix : unit.getPrefixes()) {
                result++;
            }
        }
        return result;
    }

    public String[][] getArrayUnits() {
        String[][] result = new String[getCount()][2];
        int i = 0;
        for (Unit unit : units) {
            result[i][0] = unit.getUnitName();
            result[i][1] = unit.getUnitDescription();
            i++;
            if (unit.getPrefixes() == null) continue;
            for (Prefix prefix : unit.getPrefixes()) {
                result[i][0] = prefix.getPrefixName() + unit.getUnitName();
                result[i][1] = prefix.getPrefixDescription() + unit.getUnitDescription();
                i++;
            }
        }
        return result;
    }

    public double calculate(double number, String from, String to) {
        return (((number + getShift(from)) * getOne(from)) / getOne(to)) - getShift(to);
    }

    public double singleCalculate(double number, String operator) {
        switch (operator) {
            case "+-":
                return (-1) * number;
            default:
                return 0;
        }
    }
}