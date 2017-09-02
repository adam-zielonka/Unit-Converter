package pro.adamzielonka.converter.models.file;

import pro.adamzielonka.converter.components.LanguageMap;

public class Prefix {
    public String symbol = "";
    public LanguageMap description = new LanguageMap();
    public Double exp = 1.0;
    public Integer position = 0;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String)
            return symbol.equals(obj);
        return super.equals(obj);
    }
}
