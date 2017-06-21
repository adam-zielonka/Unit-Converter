package pro.adamzielonka.calculator.converters;

import pro.adamzielonka.calculator.abstractes.Converter;
import pro.adamzielonka.calculator.units.UnitsConverter;

public class jsonConverter extends Converter {

    UnitsConverter timeConverter;

    public jsonConverter(UnitsConverter timeConverter) {
        this.timeConverter = timeConverter;
    }

    @Override
    public double calculate(double number, String from, String to) {
        return timeConverter.convert(number,from,to);
    }
}
