package pro.adamzielonka.converter.tools;

import pro.adamzielonka.converter.activities.abstractes.ListActivity;
import pro.adamzielonka.converter.interfaces.AlertInterface;
import pro.adamzielonka.converter.interfaces.TestInterface;

public class Item {
    private int title;
    private AlertInterface.Return update;
    private AlertInterface.Alert alert;
    private AlertInterface.VoidAlert voidAlert;
    private Test test;

    private Item(int title) {
        this.title = title;
    }

    public static Item Builder(int title) {
        return new Item(title);
    }

    public Item update(AlertInterface.Return update) {
        this.update = update;
        return this;
    }

    public Item alert(AlertInterface.Alert alert) {
        this.alert = alert;
        return this;
    }

    public Item alert(AlertInterface.VoidAlert voidAlert) {
        this.voidAlert = voidAlert;
        return this;
    }

    public Item validate(TestInterface test, int error) {
        this.test = new Test(test, error);
        return this;
    }

    public void add(ListActivity activity) {
        if (update != null && alert != null) {
            activity.addItem(title, () -> update.onResult(), s -> alert.onResult(s), test);
        } else if (update != null && voidAlert != null) {
            activity.addItem(title, () -> update.onResult(), voidAlert);
        } else if (update != null) {
            activity.addItem(title, () -> update.onResult(), null, test);
        } else {
            activity.addItemTitle(title);
        }
    }
}
