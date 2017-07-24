package pro.adamzielonka.converter.models.concrete;

import java.util.Map;

public class ConcreteUnit {
    public final double one;
    public final double shift1;
    public final double shift2;
    public final String name;
    public final Map<String, String> description;
    public final int position;
    public final int basicPosition;

    public ConcreteUnit(double one, double shift1, double shift2, String name, Map<String, String> description, int position, int basicPosition) {
        this.one = one;
        this.shift1 = shift1;
        this.shift2 = shift2;
        this.name = name;
        this.description = description;
        this.position = position;
        this.basicPosition = basicPosition;
    }
}
