package pro.adamzielonka.converter.units;

import java.util.List;

import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;

public class Measures {
    private static final Measures ourInstance = new Measures();
    private List<ConcreteMeasure> concreteMeasureList;

    public static Measures getInstance() {
        return ourInstance;
    }

    private Measures() {
    }

    public void setMeasureList(List<ConcreteMeasure> measureList) {
        this.concreteMeasureList = measureList;
    }

    public List<ConcreteMeasure> getMeasureList() {
        return concreteMeasureList;
    }
}
