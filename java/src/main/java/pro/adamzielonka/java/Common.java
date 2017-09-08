package pro.adamzielonka.java;

import java.util.List;

public class Common {
    @SuppressWarnings("UnusedParameters")
    public static <T> T getItself(T itself, T dummy) {
        return itself;
    }

    public static <T> T findElement(Object o, List<T> list) {
        for (T t : list) if (t.equals(o)) return t;
        return null;
    }
}
