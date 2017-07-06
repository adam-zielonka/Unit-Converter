package pro.adamzielonka.converter.units.user;

@SuppressWarnings({"FieldCanBeLocal"})
public class Prefix {
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
