package pro.adamzielonka.converter.tools;

import java.util.List;

public class Tests {
    public static boolean isUnique(Object o, List list) {
        for (Object l : list) if (l.equals(o)) return false;
        return true;
    }
}
