package pro.adamzielonka.converter.models.file;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.components.LanguageMap;

public class Unit {
    public String symbol = "";
    public LanguageMap descriptionPrefix = new LanguageMap();
    public LanguageMap description = new LanguageMap();
    public Integer position = 0;
    public Double expBase = 10.0;
    public Double one = 1.0;
    public Double shift = 0.0;
    public Double shift2 = 0.0;
    public List<Prefix> prefixes = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String)
            return symbol.equals(obj);
        return super.equals(obj);
    }
}