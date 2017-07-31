package pro.adamzielonka.converter.interfaces;

import android.widget.TextView;

public interface ArrayAdapterInterface<T> {
    void setView(T item, TextView textPrimary, TextView textSecondary);
}
