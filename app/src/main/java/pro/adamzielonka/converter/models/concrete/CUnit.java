package pro.adamzielonka.converter.models.concrete;

import java.util.Map;

public class CUnit {
    public final Double one;
    public final Double shift1;
    public final Double shift2;
    public final String name;
    public final Map<String, String> description;
    public final Integer position;
    public final Integer basicPosition;

    public CUnit(Double one, Double shift1, Double shift2, String name,
                 Map<String, String> description, Integer position, Integer basicPosition) {
        this.one = one;
        this.shift1 = shift1;
        this.shift2 = shift2;
        this.name = name;
        this.description = description;
        this.position = position;
        this.basicPosition = basicPosition;
    }
}
