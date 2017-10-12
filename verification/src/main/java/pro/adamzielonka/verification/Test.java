package pro.adamzielonka.verification;

public class Test<T> {
    public final String error;
    private final ObjectTest<T> test;

    public Test(ObjectTest<T> test, String error) {
        this.error = error;
        this.test = test;
    }

    public boolean isTest(T o) {
        return test.onTest(o);
    }

    public interface VoidTest {
        boolean onTest();
    }

    public interface ObjectTest<T> {
        boolean onTest(T object);
    }
}
