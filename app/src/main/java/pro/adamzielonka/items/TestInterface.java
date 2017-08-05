package pro.adamzielonka.items;

public interface TestInterface {
    interface Test {
        boolean onTest();
    }

    interface ObjectTest {
        boolean onTest(Object object);
    }
}
