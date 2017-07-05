package pro.adamzielonka.converter.units.user;

@SuppressWarnings({"FieldCanBeLocal"})
class Prefix {
    private String prefixName = "";
    private String prefixDescription = "";
    private Double prefixExponent = 1.0;
    private Integer unitPosition = 0;

    String getPrefixName() {
        return prefixName;
    }

    String getPrefixDescription() {
        return prefixDescription;
    }

    Integer getUnitPosition() {
        return unitPosition;
    }

    Double getPrefixExponent() {
        return prefixExponent;
    }
}
