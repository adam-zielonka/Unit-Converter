package pro.adamzielonka.converter.units.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.concrete.ConcreteUnit;

@SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class Measure {
    private String name = "";
    private Integer displayFrom = 0;
    private Integer displayTo = 1;

    public List<Unit> getUnits() {
        return units;
    }

    private List<Unit> units = new ArrayList<>();

    private int getPosition(int i, int position) {
        return (2 * i) + ((position != 0) ? (((-2) * position) + 1) : 0);
    }

    private List<ConcreteUnit> setConcreteUnits() {
        List<ConcreteUnit> concreteUnits = new ArrayList<>();
        int i = 0;
        for (Unit unit : units) {
            concreteUnits.add(new ConcreteUnit(
                    unit.getOne(),
                    unit.getShift(),
                    unit.getShift2(),
                    unit.getUnitName(),
                    unit.getUnitDescriptionFirst() + unit.getUnitDescription(),
                    getPosition(i, unit.getUnitPosition()))
            );
            i++;
            for (Prefix prefix : unit.getPrefixes()) {
                concreteUnits.add(new ConcreteUnit(
                        unit.getOne() * Math.pow(unit.getPrefixBase(), prefix.getPrefixExponent()),
                        unit.getShift(),
                        unit.getShift2(),
                        prefix.getPrefixName() + unit.getUnitName(),
                        unit.getUnitDescriptionFirst() + prefix.getPrefixDescription() + unit.getUnitDescription(),
                        getPosition(i, prefix.getUnitPosition()))
                );
                i++;
            }
        }
        Collections.sort(concreteUnits, (unit1, unit2) -> ((Integer) unit1.getPosition()).compareTo(unit2.getPosition()));
        return concreteUnits;
    }

    private List<ConcreteUnit> getConcreteUnits() {
           return setConcreteUnits();
    }

    public ConcreteMeasure getConcreteMeasure() {
        return new ConcreteMeasure(
                getName(),
                getDisplayFrom(),
                getDisplayTo(),
                getConcreteUnits()
        );
    }

    private Integer getDisplayFrom() {
        if (displayFrom >= 0 && displayFrom < getCount())
            return displayFrom;
        return 0;
    }

    private Integer getDisplayTo() {
        if (displayTo >= 0 && displayTo < getCount())
            return displayTo;
        if (getCount() > 1)
            return 1;
        return 0;
    }

    public String getName() {
        return name;
    }

    private int setCount() {
        int count = 0;
        for (Unit unit : units) {
            count++;
            for (Prefix prefix : unit.getPrefixes()) {
                count++;
            }
        }
        return count;
    }

    private int getCount() {
        return setCount();
    }
}