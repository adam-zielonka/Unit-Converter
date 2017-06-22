package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal"})
public class Units {
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("displayFrom")
    @Expose
    private Integer displayFrom = 0;
    @SerializedName("displayTo")
    @Expose
    private Integer displayTo = 1;
    @SerializedName("units")
    @Expose
    private List<Unit> units = null;

    public Integer getDisplayFrom() {
        if (displayFrom >= 0 && displayFrom < getCount())
            return displayFrom;
        return 0;
    }

    public Integer getDisplayTo() {
        if (displayTo >= 0 && displayTo < getCount())
            return displayTo;
        if(getCount()>1)
            return 1;
        return 0;
    }

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
        int count = getCount();
        String[][] result = new String[count][2];
        int[] positions = new int[count];
        int i = 0;
        for (Unit unit : units) {
            result[i][0] = unit.getUnitName();
            result[i][1] = unit.getUnitDescriptionFirst() + unit.getUnitDescription();
            positions[i] = unit.getUnitPosition();
            i++;
            if (unit.getPrefixes() == null) continue;
            for (Prefix prefix : unit.getPrefixes()) {
                result[i][0] = prefix.getPrefixName() + unit.getUnitName();
                result[i][1] = unit.getUnitDescriptionFirst() + prefix.getPrefixDescription() + unit.getUnitDescription();
                positions[i] = prefix.getUnitPosition();
                i++;
            }
        }

        boolean finished = true;

        while (finished) {
            finished = false;
            for (i = 1; i < count; i++) {
                if (positions[i] > 0) {
                    finished = true;
                    String[] resultTemp = result[i];
                    result[i] = result[i - 1];
                    result[i - 1] = resultTemp;

                    positions[i]--;
                    int positionTemp = positions[i];
                    positions[i] = positions[i - 1];
                    positions[i - 1] = positionTemp;
                }
            }
            for (i = count - 2; i >= 0; i--) {
                if (positions[i] < 0) {
                    finished = true;
                    String[] resultTemp = result[i];
                    result[i] = result[i + 1];
                    result[i + 1] = resultTemp;

                    positions[i]++;
                    int positionTemp = positions[i];
                    positions[i] = positions[i + 1];
                    positions[i + 1] = positionTemp;
                }
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