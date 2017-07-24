package pro.adamzielonka.converter.models.concrete;

import java.util.List;
import java.util.Map;

import static pro.adamzielonka.converter.tools.Language.getLanguageWords;

public class ConcreteMeasure {
    private final Map<String, String> name;
    public final String global;
    public final Integer displayFrom;
    public final Integer displayTo;
    public final List<ConcreteUnit> concreteUnits;
    public String concreteFileName;
    public String userFileName;

    public ConcreteMeasure(Map<String, String> name, String global, Integer displayFrom, Integer displayTo, List<ConcreteUnit> concreteUnits) {
        this.name = name;
        this.global = global;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.concreteUnits = concreteUnits;
    }

    public ConcreteMeasure(Map<String, String> name, String global, Integer displayFrom, Integer displayTo, List<ConcreteUnit> concreteUnits, String concreteFileName, String userFileName) {
        this.name = name;
        this.global = global;
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

    public String getName(String langCode) {
        return getLanguageWords(name, langCode, global);
    }

    public String getWords(Map<String, String> map, String langCode) {
        return getLanguageWords(map, langCode, global);
    }
}
