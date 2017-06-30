package pro.adamzielonka.converter.units;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
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
    private List<Unit> units = new ArrayList<>();

    private String[][] arrayUnits = null;
    private int count = -1;

    public Integer getDisplayFrom() {
        if (displayFrom >= 0 && displayFrom < getCount())
            return displayFrom;
        return 0;
    }

    public Integer getDisplayTo() {
        if (displayTo >= 0 && displayTo < getCount())
            return displayTo;
        if (getCount() > 1)
            return 1;
        return 0;
    }

    public String getName() {
        return name;
    }

    private void setCount() {
        count = 0;
        for (Unit unit : units) {
            count++;
            for (Prefix prefix : unit.getPrefixes()) {
                count++;
            }
        }
    }

    private int getCount() {
        if (count == -1)
            setCount();
        return count;
    }

    public void setArrayUnits() {
        int count = getCount();
        arrayUnits = new String[count][2];
        int[] positions = new int[count];
        int i = 0;
        for (Unit unit : units) {
            arrayUnits[i][0] = unit.getUnitName();
            arrayUnits[i][1] = unit.getUnitDescriptionFirst() + unit.getUnitDescription();
            positions[i] = 2 * (count - i);
            if (unit.getUnitPosition() != 0)
                positions[i] += 2 * unit.getUnitPosition() - 1;
            i++;
            for (Prefix prefix : unit.getPrefixes()) {
                arrayUnits[i][0] = prefix.getPrefixName() + unit.getUnitName();
                arrayUnits[i][1] = unit.getUnitDescriptionFirst() + prefix.getPrefixDescription() + unit.getUnitDescription();
                positions[i] = 2 * (count - i);
                if (prefix.getUnitPosition() != 0)
                    positions[i] += 2 * prefix.getUnitPosition() - 1;
                i++;
            }
        }

        for (int j = 1; j < count - 1; j++) {
            boolean sorted = true;
            for (int k = 0; k < count - j; k++) {
                if (positions[k] < positions[k + 1]) {
                    sorted = false;
                    String[] resultTemp = arrayUnits[k];
                    arrayUnits[k] = arrayUnits[k + 1];
                    arrayUnits[k + 1] = resultTemp;

                    int positionTemp = positions[k];
                    positions[k] = positions[k + 1];
                    positions[k + 1] = positionTemp;
                }
            }
            if (sorted) break;
        }
    }

    public String[][] getArrayUnits() {
        if (arrayUnits == null)
            setArrayUnits();
        return arrayUnits;
    }

    private ConcreteUnit getUnit(String unitName) {
        for (Unit unit : units) {
            if (unit.getUnitName().equals(unitName)) {
                return new ConcreteUnit(unit.getOne(), unit.getShift(), unit.getShift2());
            }
            for (Prefix prefix : unit.getPrefixes()) {
                if (unitName.equals(prefix.getPrefixName() + unit.getUnitName())) {
                    return new ConcreteUnit(
                            unit.getOne() * Math.pow(unit.getPrefixBase(), prefix.getPrefixExponent()),
                            unit.getShift(), unit.getShift2()
                    );
                }
            }
        }
        return new ConcreteUnit();
    }

    public double calculate(double number, String from, String to) {
        ConcreteUnit unitFrom = getUnit(from);
        ConcreteUnit unitTo = getUnit(to);

        return ((((number + unitFrom.getShift1()) * unitFrom.getOne()) + unitFrom.getShift2() - unitTo.getShift2()) / unitTo.getOne()) - unitTo.getShift1();
    }
}