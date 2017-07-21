package pro.adamzielonka.converter.models.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pro.adamzielonka.converter.models.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;

@SuppressWarnings({"FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class Measure {
    public void setName(String name) {
        this.name = name;
    }

    private String name = "";
    private String author = "";
    private Integer version = 0;
    private String cloudID = "";
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
                    unit.getSymbol(),
                    unit.getDescriptionPrefix() + unit.getDescription(),
                    getPosition(i, unit.getPosition()), 2 * i)
            );
            i++;
            for (Prefix prefix : unit.getPrefixes()) {
                concreteUnits.add(new ConcreteUnit(
                        unit.getOne() * Math.pow(unit.getExpBase(), prefix.getExp()),
                        unit.getShift(),
                        unit.getShift2(),
                        prefix.getSymbol() + unit.getSymbol(),
                        unit.getDescriptionPrefix() + prefix.getDescription() + unit.getDescription(),
                        getPosition(i, prefix.getPosition()), 2 * i)
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

    public ConcreteMeasure getConcreteMeasure(String concreteFile, String userFile) {
        return new ConcreteMeasure(
                getName(),
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

    public void setDisplayFrom(Integer displayFrom) {
        this.displayFrom = displayFrom;
    }

    public void setDisplayTo(Integer displayTo) {
        this.displayTo = displayTo;
    }

    public String getCloudID() {
        return cloudID;
    }

    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}