package pro.adamzielonka.items.classes;

import pro.adamzielonka.items.interfaces.TestInterface;

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
