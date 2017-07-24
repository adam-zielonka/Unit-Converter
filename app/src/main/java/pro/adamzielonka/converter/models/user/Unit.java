package pro.adamzielonka.converter.models.user;

import java.util.ArrayList;
import java.util.List;

public class Unit {
    public String symbol = "";
    public String descriptionPrefix = "";
    public String description = "";
    public Integer position = 0;
    public Double expBase = 10.0;
    public Double one = 1.0;
    public Double shift = 0.0;
    public Double shift2 = 0.0;
    public List<Prefix> prefixes = new ArrayList<>();

    public String getFullDescription() {
        return descriptionPrefix + description;
    }
}