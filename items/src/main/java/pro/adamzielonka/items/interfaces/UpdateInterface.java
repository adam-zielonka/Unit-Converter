package pro.adamzielonka.items.interfaces;

import android.support.annotation.StringRes;

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

    interface StringResUpdate {
        @StringRes
        int onUpdate();
    }

    interface PositionUpdate {
        Integer onUpdate();
    }
}
