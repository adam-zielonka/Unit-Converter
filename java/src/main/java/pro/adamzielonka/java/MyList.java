package pro.adamzielonka.java;

import java.util.ArrayList;

public class MyList<T> extends ArrayList<T> {

    public interface Action<T> {
        void accept(T t);
    }

    public void myForEach(Action<T> action) {
        for (T t : this)
            action.accept(t);
    }
}
