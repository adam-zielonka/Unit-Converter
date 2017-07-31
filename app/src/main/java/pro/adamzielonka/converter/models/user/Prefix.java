package pro.adamzielonka.converter.models.user;

import java.util.HashMap;
import java.util.Map;

public class Prefix {
    public String symbol = "";
    public Map<String, String> description = new HashMap<>();
    public Double exp = 1.0;
    public Integer position = 0;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String)
            return symbol.equals(obj);
        return super.equals(obj);
    }
}
