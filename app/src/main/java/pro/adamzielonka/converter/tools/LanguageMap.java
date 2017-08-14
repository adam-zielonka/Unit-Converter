package pro.adamzielonka.converter.tools;

import java.util.HashMap;

public class LanguageMap extends HashMap<String, String> {

    public String get(String key, String global) {
        return containsKey(key) ? get(key) : containsKey(global) ? get(global) : "";
    }
}
