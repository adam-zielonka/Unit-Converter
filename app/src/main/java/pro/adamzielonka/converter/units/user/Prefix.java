package pro.adamzielonka.converter.units.user;

@SuppressWarnings({"FieldCanBeLocal"})
public class Prefix {
    private String symbol = "";
    private String description = "";
    private Double exp = 1.0;
    private Integer position = 0;

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPosition() {
        return position;
    }

    public Double getExp() {
        return exp;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExp(Double exp) {
        this.exp = exp;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
