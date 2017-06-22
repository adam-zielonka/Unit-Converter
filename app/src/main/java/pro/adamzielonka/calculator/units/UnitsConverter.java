package pro.adamzielonka.calculator.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import pro.adamzielonka.calculator.abstractes.Converter;

@SuppressWarnings("CanBeFinal")
public class UnitsConverter extends Converter {
    @SerializedName("units")
    @Expose
    private List<Unit> units = null;

    private double getOne(String unitName) {
        for (Unit unit : units) {
            if (unit.getUnitName().equals(unitName)) {
                return unit.getOne();
            }
            if(unit.getPrefixes() == null) continue;
            for (Prefix prefix : unit.getPrefixes()) {
                if(unitName.equals(prefix.getPrefixName()+unit.getUnitName()))
                {
                    return  unit.getOne()*Math.pow(unit.getPrefixBase(),prefix.getPrefixExponent());
                }
            }
        }
        return 1.0;
    }

    @Override
    public double calculate(double number, String from, String to) {
        return ((number) * getOne(from)) / getOne(to);
    }
}