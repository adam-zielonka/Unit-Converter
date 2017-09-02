package pro.adamzielonka.converter.models.concrete;

import pro.adamzielonka.converter.components.LanguageMap;

public class CUnit {
    public final Double one;
    public final Double shift1;
    public final Double shift2;
    public final String name;
    public final LanguageMap description;
    public final Integer position;
    public final Integer basicPosition;

    public CUnit(Double one, Double shift1, Double shift2, String name,
                 LanguageMap description, Integer position, Integer basicPosition) {
        this.one = one;
        this.shift1 = shift1;
        this.shift2 = shift2;
        this.name = name;
        this.description = description;
        this.position = position;
        this.basicPosition = basicPosition;
    }
}
