package pro.adamzielonka.converter.units.concrete;

import java.util.List;

public class ConcreteMeasure {
    private final String name;
    private final Integer displayFrom;
    private final Integer displayTo;
    private final List<ConcreteUnit> concreteUnits;
    private String concreteFileName;
    private String userFileName;

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

    public void setConcreteFileName(String concreteFileName) {
        this.concreteFileName = concreteFileName;
    }

    public String getConcreteFileName() {
        return concreteFileName;
    }

    public String getUserFileName() {
        return userFileName;
    }

    public void setUserFileName(String userFileName) {
        this.userFileName = userFileName;
    }

    public boolean isCorrect() {
        return concreteUnits.size() != 0;
    }

    public String getUnitsOrder() {
        StringBuilder order = new StringBuilder("");
        for (ConcreteUnit concreteUnit : getConcreteUnits()) {
            order.append(concreteUnit.getName());
            order.append(" ");
        }
        return order.toString();
    }
}
