package pro.adamzielonka.converter.models.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.adamzielonka.converter.models.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.models.concrete.ConcreteUnit;

import static pro.adamzielonka.converter.tools.Converter.getLanguageWords;

public class Measure {
    public String global = "en";
    private Map<String, String> name = new HashMap<>();
    public String author = "";
    public Integer version = 0;
    public String cloudID = "";
    public List<Unit> units = new ArrayList<>();
    public Integer displayFrom = 0;
    public Integer displayTo = 1;

    private int getPosition(int i, int position) {
        return (2 * i) + ((position != 0) ? (((-2) * position) + 1) : 0);
    }

    private List<ConcreteUnit> getConcreteUnits() {
        List<ConcreteUnit> concreteUnits = new ArrayList<>();
        int i = 0;
        for (Unit unit : units) {
            concreteUnits.add(new ConcreteUnit(
                    unit.one,
                    unit.shift,
                    unit.shift2,
                    unit.symbol,
                    getCUnitName(unit.descriptionPrefix, unit.description),
                    getPosition(i, unit.position), 2 * i)
            );
            i++;
            for (Prefix prefix : unit.prefixes) {
                concreteUnits.add(new ConcreteUnit(
                        unit.one * Math.pow(unit.expBase, prefix.exp),
                        unit.shift,
                        unit.shift2,
                        prefix.symbol + unit.symbol,
                        getCPrefixName(unit.descriptionPrefix, prefix.description, unit.description),
                        getPosition(i, prefix.position), 2 * i)
                );
                i++;
            }
        }
        Collections.sort(concreteUnits, (unit1, unit2) -> ((Integer) unit1.position).compareTo(unit2.position));
        return concreteUnits;
    }

    private Map<String, String> getCUnitName(Map<String, String> descriptionPrefix, Map<String, String> description) {
        Map<String, String> map = new HashMap<>(descriptionPrefix);
        for (Map.Entry<String, String> e : description.entrySet())
            map.put(e.getKey(), getKey(map, e.getKey()) + e.getValue());
        return map;
    }

    private Map<String, String> getCPrefixName(Map<String, String> descriptionPrefix, Map<String, String> prefixDescription, Map<String, String> description) {
        Map<String, String> map = new HashMap<>(descriptionPrefix);
        for (Map.Entry<String, String> e : prefixDescription.entrySet())
            map.put(e.getKey(), getKey(map, e.getKey()) + e.getValue());
        for (Map.Entry<String, String> e : description.entrySet())
            map.put(e.getKey(), getKey(map, e.getKey()) + e.getValue());
        return map;
    }

    private String getKey(Map<String, String> map, String key) {
        return map.containsKey(key) ? map.get(key) : "";
    }

    public ConcreteMeasure getConcreteMeasure() {
        return new ConcreteMeasure(
                name,
                global,
                getDisplayFrom(),
                getDisplayTo(),
                getConcreteUnits()
        );
    }

    public ConcreteMeasure getConcreteMeasure(String concreteFile, String userFile) {
        return new ConcreteMeasure(
                name,
                global,
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

    public String getName(String langCode) {
        return getLanguageWords(name, langCode, global);
    }

    public String getWords(Map<String, String> map, String langCode) {
        return getLanguageWords(map, langCode, global);
    }

    public void setName(String langCode, String name) {
        this.name.put(langCode, name);
    }
}