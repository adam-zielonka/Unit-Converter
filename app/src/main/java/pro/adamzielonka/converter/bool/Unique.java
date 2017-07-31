package pro.adamzielonka.converter.bool;

import java.util.List;

public class Unique<T> extends Test {
    public List<T> list;

    public Unique(List<T> list, int error) {
        super(error);
        this.list = list;
    }

    @Override
    public boolean isTest(Object o) {
        for (T t : list) {
            if (t.equals(o)) return false;
        }
        return true;
    }
}