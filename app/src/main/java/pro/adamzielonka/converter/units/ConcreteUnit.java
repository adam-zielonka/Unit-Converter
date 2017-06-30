package pro.adamzielonka.converter.units;

class ConcreteUnit {

    private double one;
    private double shift1;
    private double shift2;

    ConcreteUnit(){
        one = 1.0;
        shift1 = 0.0;
        shift2 = 0.0;
    }

    ConcreteUnit(double one, double shift1, double shift2){
        this.one = one;
        this.shift1 = shift1;
        this.shift2 = shift2;
    }

    double getOne() {
        return one;
    }

    double getShift1() {
        return shift1;
    }

    double getShift2() {
        return shift2;
    }
}
