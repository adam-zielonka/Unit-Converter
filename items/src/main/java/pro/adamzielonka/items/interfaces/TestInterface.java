package pro.adamzielonka.items.interfaces;

public interface TestInterface {
    interface Test {
        boolean onTest();
    }

    interface ObjectTest {
        boolean onTest(Object object);
    }
}
