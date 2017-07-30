package pro.adamzielonka.converter.bool;

import java.util.List;

public abstract class Unique {
    public List list;
    public int error;

    Unique(List list, int error) {
        this.list = list;
        this.error = error;
    }

    public abstract boolean isUnique(String text);
}
