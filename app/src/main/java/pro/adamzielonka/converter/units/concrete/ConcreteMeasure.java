package pro.adamzielonka.converter.units.concrete;

import java.util.List;

public class ConcreteMeasure {
    private final String name;
    private final Integer displayFrom;
    private final Integer displayTo;
    private final List<ConcreteUnit> concreteUnits;

    public ConcreteMeasure(String name, Integer displayFrom, Integer displayTo, List<ConcreteUnit> concreteUnits) {
        this.name = name;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.concreteUnits = concreteUnits;
    }

    public String getName() {
        return name;
    }

    public Integer getDisplayFrom() {
        return displayFrom;
    }

    public Integer getDisplayTo() {
        return displayTo;
    }

    public List<ConcreteUnit> getConcreteUnits() {
        return concreteUnits;
    }
}
