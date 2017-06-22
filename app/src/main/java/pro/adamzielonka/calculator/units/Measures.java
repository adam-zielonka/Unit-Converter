package pro.adamzielonka.calculator.units;

import java.util.List;

public class Measures {
    private static final Measures ourInstance = new Measures();
    private List<Units> unitsList;

    public static Measures getInstance() {
        return ourInstance;
    }

    private Measures() {
    }

    public void setUnitsList(List<Units> unitsList) {
        this.unitsList = unitsList;
    }

    public List<Units> getUnitsList() {
        return unitsList;
    }
}
