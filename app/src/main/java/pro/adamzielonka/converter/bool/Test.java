package pro.adamzielonka.converter.bool;

public abstract class Test {
    public int error;

    Test(int error) {
        this.error = error;
    }

    public abstract boolean isTest(Object o);
}
