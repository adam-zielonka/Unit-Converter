package pro.adamzielonka.items.tools;

import java.util.List;

public class Tests {
    public static <T> boolean isUnique(Object o, List<T> list) {
        for (T t : list) if (t.equals(o)) return false;
        return true;
    }
}
