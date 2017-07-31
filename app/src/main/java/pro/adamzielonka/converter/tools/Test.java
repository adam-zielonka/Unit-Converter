package pro.adamzielonka.converter.tools;

import pro.adamzielonka.converter.interfaces.TestInterface;

public class Test {
    public int error;
    private TestInterface test;

    public Test(TestInterface test, int error) {
        this.error = error;
        this.test = test;
    }

    public boolean isTest(Object o) {
        return test.onTest(o);
    }
}
