package pro.adamzielonka.items;

public class Test {
    public String error;
    private TestInterface.ObjectTest test;

    public Test(TestInterface.ObjectTest test, String error) {
        this.error = error;
        this.test = test;
    }

    public boolean isTest(Object o) {
        return test.onTest(o);
    }
}
