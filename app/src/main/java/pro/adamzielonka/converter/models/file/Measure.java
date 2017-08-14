package pro.adamzielonka.converter.models.file;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.concrete.CUnit;
import pro.adamzielonka.converter.tools.LanguageMap;

public class Measure {
    public String global = "en";
    public LanguageMap name = new LanguageMap();
    public List<String> author = new ArrayList<>();
    public Long version = 0L;
    public String cloudID = "";
    public List<Unit> units = new ArrayList<>();
    public Integer displayFrom = 0;
    public Integer displayTo = 1;

    private int getPosition(int i, int position) {
        return (2 * i) + ((position != 0) ? (((-2) * position) + 1) : 0);
    }

    private List<CUnit> getConcreteUnits() {
        List<CUnit> cUnits = new ArrayList<>();
        int i = 0;
        for (Unit unit : units) {
            cUnits.add(new CUnit(
                    unit.one,
                    unit.shift,
                    unit.shift2,
                    unit.symbol,
                    getCUnitName(unit.descriptionPrefix, unit.description),
                    getPosition(i, unit.position), 2 * i)
            );
            i++;
            for (Prefix prefix : unit.prefixes) {
                cUnits.add(new CUnit(
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
        Collections.sort(cUnits, (unit1, unit2) -> unit1.position.compareTo(unit2.position));
        return cUnits;
    }

    private LanguageMap getCUnitName(LanguageMap descriptionPrefix,
                                             LanguageMap description) {
        LanguageMap map = new LanguageMap();
        for (Map.Entry<String, String> e : descriptionPrefix.entrySet())
            map.put(e.getKey(), e.getValue() + getValue(description, e.getKey()));
        for (Map.Entry<String, String> e : description.entrySet())
            map.put(e.getKey(), getValue(descriptionPrefix, e.getKey()) + e.getValue());
        return map;
    }

    private LanguageMap getCPrefixName(LanguageMap descriptionPrefix,
                                               LanguageMap prefixDescription,
                                               LanguageMap description) {
        LanguageMap map = new LanguageMap();
        for (Map.Entry<String, String> e : descriptionPrefix.entrySet())
            map.put(e.getKey(), e.getValue()
                    + getValue(prefixDescription, e.getKey())
                    + getValue(description, e.getKey()));
        for (Map.Entry<String, String> e : prefixDescription.entrySet())
            map.put(e.getKey(), getValue(descriptionPrefix, e.getKey())
                    + e.getValue()
                    + getValue(description, e.getKey()));
        for (Map.Entry<String, String> e : description.entrySet())
            map.put(e.getKey(), getValue(descriptionPrefix, e.getKey())
                    + getValue(prefixDescription, e.getKey())
                    + e.getValue());
        return map;
    }

    private String getValue(LanguageMap map, String key) {
        return map.containsKey(key) ? map.get(key) : (map.containsKey(global) ? map.get(global) : "");
    }

    private Integer getValueInt(Map<String, Integer> map, String key) {
        return map.containsKey(key) ? map.get(key) : 0;
    }

    private Map<String, Integer> getLanguages(List<Unit> units, LanguageMap name) {
        Map<String, Integer> map = new HashMap<>();
        for (Unit unit : units) {
            for (Map.Entry<String, String> e : unit.description.entrySet())
                map.put(e.getKey(), getValueInt(map, e.getKey()) + 1);
            for (Map.Entry<String, String> e : unit.descriptionPrefix.entrySet())
                map.put(e.getKey(), getValueInt(map, e.getKey()) + 1);
            for (Prefix prefix : unit.prefixes)
                for (Map.Entry<String, String> e : prefix.description.entrySet())
                    map.put(e.getKey(), getValueInt(map, e.getKey()) + 1);
        }
        for (Map.Entry<String, String> e : name.entrySet())
            map.put(e.getKey(), getValueInt(map, e.getKey()) + 1);
        return map;
    }

    private void getTranslation(Context context, ArrayList<String[]> list, Integer id, LanguageMap map, String langCode) {
        String[] s = new String[3];
        s[0] = map.containsKey(global) ? map.get(global) : "";
        s[1] = map.containsKey(langCode) ? map.get(langCode) : context.getString(R.string.language_repeat_tag);
        s[2] = id.toString();
        if (!s[0].isEmpty() || !s[1].equals(context.getString(R.string.language_repeat_tag))) {
            if (s[1].isEmpty()) s[1] = context.getString(R.string.language_empty_tag);
            list.add(s);
        }
    }

    public ArrayList<String[]> getLanguagesStr(Context context, String langCode) {
        ArrayList<String[]> list = new ArrayList<>();
        int id = 0;
        getTranslation(context, list, id++, name, langCode);
        for (Unit unit : units) {
            getTranslation(context, list, id++, unit.description, langCode);
            getTranslation(context, list, id++, unit.descriptionPrefix, langCode);
            for (Prefix prefix : unit.prefixes)
                getTranslation(context, list, id++, prefix.description, langCode);
        }
        return list;
    }

    public void removeLang(String langCode) {
        name.remove(langCode);
        for (Unit unit : units) {
            unit.description.remove(langCode);
            unit.descriptionPrefix.remove(langCode);
            for (Prefix prefix : unit.prefixes)
                prefix.description.remove(langCode);
        }
    }

    public void setLanguagesStr(String langCode, Integer id, String text) {
        int i = 0;
        if (id == i++) {
            name.put(langCode, text);
            return;
        }
        for (Unit unit : units) {
            if (id == i++) {
                unit.description.put(langCode, text);
                return;
            }
            if (id == i++) {
                unit.descriptionPrefix.put(langCode, text);
                return;
            }
            for (Prefix prefix : unit.prefixes)
                if (id == i++) {
                    prefix.description.put(langCode, text);
                    return;
                }
        }
    }

    public void setRepeatStr(String langCode, Integer id) {
        int i = 0;
        if (id == i++) {
            name.remove(langCode);
            return;
        }
        for (Unit unit : units) {
            if (id == i++) {
                unit.description.remove(langCode);
                return;
            }
            if (id == i++) {
                unit.descriptionPrefix.remove(langCode);
                return;
            }
            for (Prefix prefix : unit.prefixes)
                if (id == i++) {
                    prefix.description.remove(langCode);
                    return;
                }
        }
    }

    public CMeasure getConcreteMeasure() {
        List<CUnit> cUnits = getConcreteUnits();
        return new CMeasure(
                name,
                global,
                getDisplayFrom(),
                getDisplayTo(),
                cUnits,
                getLanguages(units, name));
    }

    public CMeasure getConcreteMeasure(String concreteFile, String userFile,
                                       Boolean isOwnName, String ownName,
                                       Boolean isOwnLang, String ownLang, List<String> newLangs) {
        List<CUnit> cUnits = getConcreteUnits();
        return new CMeasure(
                name,
                global,
                getDisplayFrom(), getDisplayTo(),
                cUnits,
                getLanguages(units, name),
                concreteFile, userFile,
                isOwnName, ownName,
                isOwnLang, ownLang, newLangs);
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
        return name.get(langCode, global);
    }

    public String getWords(LanguageMap map, String langCode) {
        return map.get(langCode, global);
    }

    public void setName(String langCode, String name) {
        this.name.put(langCode, name);
    }
}