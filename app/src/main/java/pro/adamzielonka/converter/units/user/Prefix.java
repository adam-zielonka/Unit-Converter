package pro.adamzielonka.converter.units.user;

@SuppressWarnings({"FieldCanBeLocal"})
public class Prefix {
    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public void setPrefixDescription(String prefixDescription) {
        this.prefixDescription = prefixDescription;
    }

    public void setPrefixExponent(Double prefixExponent) {
        this.prefixExponent = prefixExponent;
    }

    public void setUnitPosition(Integer unitPosition) {
        this.unitPosition = unitPosition;
    }

    private String prefixName = "";
    private String prefixDescription = "";
    private Double prefixExponent = 1.0;
    private Integer unitPosition = 0;

    public String getPrefixName() {
        return prefixName;
    }

    public String getPrefixDescription() {
        return prefixDescription;
    }

    public Integer getUnitPosition() {
        return unitPosition;
    }

    public Double getPrefixExponent() {
        return prefixExponent;
    }
}
