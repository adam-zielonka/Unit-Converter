package pro.adamzielonka.converter.models.concrete;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.adamzielonka.converter.components.LanguageMap;
import pro.adamzielonka.converter.settings.Language;

import static pro.adamzielonka.converter.settings.Language.getLanguage;

public class CMeasure {
    private final LanguageMap name;
    public final String global;
    public final Integer displayFrom;
    public final Integer displayTo;
    public final List<CUnit> cUnits;
    public final Map<String, Integer> languages;
    public String concreteFileName;
    public String userFileName;
    public Boolean isOwnName = false;
    public String ownName = "";
    public Boolean isOwnLang = false;
    public String ownLang = "";
    public List<String> newLangs = new ArrayList<>();

    public CMeasure(LanguageMap name, String global, Integer displayFrom, Integer displayTo, List<CUnit> cUnits, Map<String, Integer> languages) {
        this.name = name;
        this.global = global;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.cUnits = cUnits;
        this.languages = languages;
        this.ownLang = global;
    }

    public CMeasure(LanguageMap name, String global, Integer displayFrom, Integer displayTo, List<CUnit> cUnits, Map<String, Integer> languages,
                    String concreteFileName, String userFileName, Boolean isOwnName, String ownName, Boolean isOwnLang, String ownLang, List<String> newLangs) {
        this.name = name;
        this.global = global;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.cUnits = cUnits;
        this.languages = languages;
        this.concreteFileName = concreteFileName;
        this.userFileName = userFileName;
        this.isOwnName = isOwnName;
        this.ownName = ownName;
        this.isOwnLang = isOwnLang;
        this.ownLang = ownLang;
        this.newLangs = newLangs;
    }

    public boolean isCorrect() {
        return cUnits.size() != 0;
    }

    public String getUnitsOrder() {
        StringBuilder order = new StringBuilder("");
        for (CUnit cUnit : cUnits) {
            order.append(cUnit.name);
            order.append(" ");
        }
        return order.toString();
    }

    public String getName(String langCode) {
        return name.get(langCode, global);
    }

    public String getWords(LanguageMap map, String langCode) {
        return map.get(langCode, global);
    }

    public String[] getGlobalLangs() {
        String[] langs = new String[languages.size()];
        int i = 0;
        for (Map.Entry<String, Integer> e : languages.entrySet()) {
            langs[i] = getLanguage(e.getKey());
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

    public int getOwnLangID() {
        int i = 0;
        for (Map.Entry<String, Integer> e : languages.entrySet()) {
            if (e.getKey().equals(ownLang)) return i;
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

    public String[] getUnitsSymbol() {
        String[] symbols = new String[cUnits.size()];
        for (int i = 0; i < cUnits.size(); i++) {
            symbols[i] = cUnits.get(i).name;
        }
        return symbols;
    }

    public String getOwnLang(Activity activity) {
        return isOwnLang ? ownLang != null ? ownLang : global : Language.getConverterLanguageCode(activity);
    }
}
