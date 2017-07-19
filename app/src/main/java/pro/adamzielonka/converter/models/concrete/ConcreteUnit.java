package pro.adamzielonka.converter.models.concrete;

public class ConcreteUnit {
    private final double one;
    private final double shift1;
    private final double shift2;
    private final String name;
    private final String description;
    private final int position;
    private final int basicPosition;

    public ConcreteUnit(double one, double shift1, double shift2, String name, String description, int position, int basicPosition) {
        this.one = one;
        this.shift1 = shift1;
        this.shift2 = shift2;
        this.name = name;
        this.description = description;
        this.position = position;
        this.basicPosition = basicPosition;
    }

    public double getOne() {
        return one;
    }

    public double getShift1() {
        return shift1;
    }

    public double getShift2() {
        return shift2;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPosition() {
        return position;
    }

    public int getBasicPosition() {
        return basicPosition;
    }
}
