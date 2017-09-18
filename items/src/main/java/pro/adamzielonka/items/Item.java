package pro.adamzielonka.items;

import android.view.View;

import pro.adamzielonka.items.interfaces.ActionInterface;

public class Item {

    public View view;
    private ActionInterface.VoidAction update;
    private ActionInterface.VoidAction action;
    final public boolean isEnabled;

    public void onUpdate() {
        if (update != null) update.onAction();
    }

    public void onAction() {
        if (action != null) action.onAction();
    }

    Item(View view, ActionInterface.VoidAction update, ActionInterface.VoidAction action) {
        this.view = view;
        this.update = update;
        this.action = action;
        this.isEnabled = true;
    }

    Item(View view) {
        this.view = view;
        this.isEnabled = false;
    }

}
