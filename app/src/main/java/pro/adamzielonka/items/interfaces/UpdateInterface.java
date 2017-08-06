package pro.adamzielonka.items.interfaces;

import java.util.List;

public interface UpdateInterface {
    interface ObjectUpdate {
        Object onUpdate();
    }

    interface ListUpdate {
        List onUpdate();
    }

    interface ObjectsUpdate {
        Object[] onUpdate();
    }

    interface PositionUpdate {
        Integer onUpdate();
    }
}
