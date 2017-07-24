package pro.adamzielonka.converter.models.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.adamzielonka.converter.models.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;

public class Measure {
    public String global = "en";
    public Map<String, String> name = new HashMap<>();
    public String author = "";
    public Integer version = 0;
    public String cloudID = "";
    public List<Unit> units = new ArrayList<>();
    public Integer displayFrom = 0;
    public Integer displayTo = 1;

    private int getPosition(int i, int position) {
        return (2 * i) + ((position != 0) ? (((-2) * position) + 1) : 0);
    }

    private List<ConcreteUnit> setConcreteUnits() {
        List<ConcreteUnit> concreteUnits = new ArrayList<>();
        int i = 0;
        for (Unit unit : units) {
            concreteUnits.add(new ConcreteUnit(
                    unit.one,
                    unit.shift,
                    unit.shift2,
                    unit.symbol,
                    unit.descriptionPrefix + unit.description,
                    getPosition(i, unit.position), 2 * i)
            );
            i++;
            for (Prefix prefix : unit.prefixes) {
                concreteUnits.add(new ConcreteUnit(
                        unit.one * Math.pow(unit.expBase, prefix.exp),
                        unit.shift,
                        unit.shift2,
                        prefix.symbol + unit.symbol,
                        unit.descriptionPrefix + prefix.description + unit.description,
                        getPosition(i, prefix.position), 2 * i)
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
                getGlobalName(),
                getDisplayFrom(),
                getDisplayTo(),
                getConcreteUnits()
        );
    }

    public ConcreteMeasure getConcreteMeasure(String concreteFile, String userFile) {
        return new ConcreteMeasure(
                getGlobalName(),
                getDisplayFrom(),
                getDisplayTo(),
                getConcreteUnits(),
                concreteFile,
                userFile
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

    private int getCount() {
        int count = 0;
        for (Unit unit : units) {
            count++;
            count += unit.prefixes.size();
        }
        return count;
    }

    public String getGlobalName() {
        return name.containsKey(global) ? name.get(global) : "";
    }
}