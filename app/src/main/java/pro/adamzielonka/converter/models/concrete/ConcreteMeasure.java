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
    public final Map<String, Integer> languages;
    public String concreteFileName;
    public String userFileName;

    public ConcreteMeasure(Map<String, String> name, String global, Integer displayFrom, Integer displayTo, List<ConcreteUnit> concreteUnits, Map<String, Integer> languages) {
        this.name = name;
        this.global = global;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.concreteUnits = concreteUnits;
        this.languages = languages;
    }

    public ConcreteMeasure(Map<String, String> name, String global, Integer displayFrom, Integer displayTo, List<ConcreteUnit> concreteUnits, Map<String, Integer> languages, String concreteFileName, String userFileName) {
        this.name = name;
        this.global = global;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.concreteUnits = concreteUnits;
        this.languages = languages;
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

    public String[] getGlobalLangs() {
        String[] langs = new String[languages.size()];
        int i = 0;
        for (Map.Entry<String, Integer> e : languages.entrySet()) {
            langs[i] = e.getKey();
            i++;
        }
        return langs;
    }

    public int getGlobalID() {
        int i = 0;
        for (Map.Entry<String, Integer> e : languages.entrySet()) {
            if (e.getKey().equals(global)) return i;
            i++;
        }
        return 0;
    }

    public String getGlobalFromID(int id) {
        int i = 0;
        for (Map.Entry<String, Integer> e : languages.entrySet()) {
            if (i == id) return e.getKey();
            i++;
        }
        return "";
    }
}
