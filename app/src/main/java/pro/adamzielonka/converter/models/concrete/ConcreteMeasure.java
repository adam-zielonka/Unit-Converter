package pro.adamzielonka.converter.models.concrete;

import java.util.List;

public class ConcreteMeasure {
    public final String name;
    public final Integer displayFrom;
    public final Integer displayTo;
    public final List<ConcreteUnit> concreteUnits;
    public String concreteFileName;
    public String userFileName;

    public ConcreteMeasure(String name, Integer displayFrom, Integer displayTo, List<ConcreteUnit> concreteUnits) {
        this.name = name;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.concreteUnits = concreteUnits;
    }

    public ConcreteMeasure(String name, Integer displayFrom, Integer displayTo, List<ConcreteUnit> concreteUnits, String concreteFileName, String userFileName) {
        this.name = name;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.concreteUnits = concreteUnits;
        this.concreteFileName = concreteFileName;
        this.userFileName = userFileName;
    }

    public boolean isCorrect() {
        return concreteUnits.size() != 0;
    }

    public String getUnitsOrder() {
        StringBuilder order = new StringBuilder("");
        for (ConcreteUnit concreteUnit : concreteUnits) {
            order.append(concreteUnit.name);
            order.append(" ");
        }
        return order.toString();
    }
}
