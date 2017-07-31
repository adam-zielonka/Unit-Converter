package pro.adamzielonka.converter.components;

import android.view.View;

import pro.adamzielonka.converter.interfaces.AlertInterface;

class MyView {
    public View view;
    private AlertInterface.VoidAlert update;
    private AlertInterface.VoidAlert alert;

    void onUpdate() {
        if (update != null) update.onResult();
    }

    void onAlert() {
        if (alert != null) alert.onResult();
    }

    MyView(View view, AlertInterface.VoidAlert update, AlertInterface.VoidAlert alert) {
        this.view = view;
        this.update = update;
        this.alert = alert;
    }
}
