package pro.adamzielonka.calculator.units;

import java.util.List;

public class UnitsList {
    private static final UnitsList ourInstance = new UnitsList();
    private List<UnitsConverter> unitsConverterList;

    public static UnitsList getInstance() {
        return ourInstance;
    }

    private UnitsList() {
    }

    public void setUnitsConverterList(List<UnitsConverter> unitsConverterList) {
        this.unitsConverterList = unitsConverterList;
    }

    public List<UnitsConverter> getUnitsConverterList() {
        return unitsConverterList;
    }
}
