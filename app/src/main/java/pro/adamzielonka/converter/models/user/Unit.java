package pro.adamzielonka.converter.models.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unit {
    public String symbol = "";
    public Map<String, String> descriptionPrefix = new HashMap<>();
    public Map<String, String> description = new HashMap<>();
    public Integer position = 0;
    public Double expBase = 10.0;
    public Double one = 1.0;
    public Double shift = 0.0;
    public Double shift2 = 0.0;
    public List<Prefix> prefixes = new ArrayList<>();
}