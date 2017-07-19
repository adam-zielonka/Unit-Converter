package pro.adamzielonka.converter.models.user;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal"})
public class Unit {
    private String symbol = "";
    private String descriptionPrefix = "";
    private String description = "";
    private Integer position = 0;
    private Double expBase = 10.0;
    private Double one = 1.0;
    private Double shift = 0.0;
    private Double shift2 = 0.0;
    private List<Prefix> prefixes = new ArrayList<>();

    public List<Prefix> getPrefixes() {
        return prefixes;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescriptionPrefix() {
        return descriptionPrefix;
    }

    public String getDescription() {
        return description;
    }

    public String getFullDescription() {
        return descriptionPrefix + description;
    }

    public Integer getPosition() {
        return position;
    }

    public Double getOne() {
        return one;
    }

    public Double getShift() {
        return shift;
    }

    public Double getShift2() {
        return shift2;
    }

    public Double getExpBase() {
        return expBase;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setDescriptionPrefix(String descriptionPrefix) {
        this.descriptionPrefix = descriptionPrefix;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setExpBase(Double expBase) {
        this.expBase = expBase;
    }

    public void setOne(Double one) {
        this.one = one;
    }

    public void setShift(Double shift) {
        this.shift = shift;
    }

    public void setShift2(Double shift2) {
        this.shift2 = shift2;
    }

    public void setPrefixes(List<Prefix> prefixes) {
        this.prefixes = prefixes;
    }
}