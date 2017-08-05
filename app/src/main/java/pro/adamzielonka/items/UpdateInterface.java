package pro.adamzielonka.items;

import java.util.List;

public interface UpdateInterface {
    interface ObjectUpdate {
        Object onUpdate();
    }

    interface ListUpdate {
        List onUpdate();
    }
}
