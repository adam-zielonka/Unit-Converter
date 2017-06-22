package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import pro.adamzielonka.calculator.abstractes.Converter;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal"})
public class UnitsConverter extends Converter {
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

    public String[] getArrayUnitsName() {
        String[] results = new String[getCount()];
        int i = 0;
        for (Unit unit : units) {
            results[i] = unit.getUnitName();
            i++;
            if (unit.getPrefixes() == null) continue;
            for (Prefix prefix : unit.getPrefixes()) {
                results[i] = prefix.getPrefixName()+unit.getUnitName();
                i++;
            }
        }
        return results;
    }

    public String[] getArrayUnitsDescription() {
        String[] results = new String[getCount()];
        int i = 0;
        for (Unit unit : units) {
            results[i] = unit.getUnitDescription();
            i++;
            if (unit.getPrefixes() == null) continue;
            for (Prefix prefix : unit.getPrefixes()) {
                results[i] = prefix.getPrefixDescription()+unit.getUnitDescription();
                i++;
            }
        }
        return results;
    }

    @Override
    public double calculate(double number, String from, String to) {
        return ((number) * getOne(from)) / getOne(to);
    }
}