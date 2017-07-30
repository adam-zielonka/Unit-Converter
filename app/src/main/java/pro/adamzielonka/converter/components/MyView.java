package pro.adamzielonka.converter.components;

import android.view.View;

import pro.adamzielonka.converter.interfaces.IAlert;

class MyView {
    public View view;
    private IAlert.IVoidAlert update;
    private IAlert.IVoidAlert alert;

    void onUpdate() {
        if (update != null) update.onResult();
    }

    void onAlert() {
        if (alert != null) alert.onResult();
    }

    MyView(View view, IAlert.IVoidAlert update, IAlert.IVoidAlert alert) {
        this.view = view;
        this.update = update;
        this.alert = alert;
    }
}
