package pro.adamzielonka.verification;

import java.util.List;

public class Tests {
    public static <L, T> boolean isUnique(T t, List<L> list) {
        for (L l : list) if (l.equals(t)) return false;
        return true;
    }
}
