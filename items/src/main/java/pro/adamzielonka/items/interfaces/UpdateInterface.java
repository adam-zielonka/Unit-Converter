package pro.adamzielonka.items.interfaces;

import android.support.annotation.StringRes;

public interface UpdateInterface {

    interface Update<T> {
        T onUpdate();
    }

    interface StringResUpdate {
        @StringRes
        int onUpdate();
    }
}
