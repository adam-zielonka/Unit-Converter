package pro.adamzielonka.converter.tools;

import android.content.Intent;
import android.widget.ArrayAdapter;

import pro.adamzielonka.converter.activities.abstractes.ListActivity;
import pro.adamzielonka.converter.interfaces.AlertInterface;
import pro.adamzielonka.converter.interfaces.TestInterface;

public class Item {
    private Integer title;
    private AlertInterface.Return update;
    private AlertInterface.ReturnList listUpdate;
    private AlertInterface.Alert alert;
    private AlertInterface.VoidAlert voidAlert;
    private Test test;
    private ArrayAdapter adapter;
    private Intent intent;
    private int requestCode;
    private TestInterface.Test condition;
    private AlertInterface.Return elseUpdate;

    private Item(Integer title) {
        this.title = title;
    }

    public static Item Builder(int title) {
        return new Item(title);
    }

    public Item update(AlertInterface.Return update) {
        this.update = update;
        return this;
    }

    public Item elseUpdate(AlertInterface.Return update) {
        this.elseUpdate = update;
        return this;
    }

    public Item update(AlertInterface.ReturnList listUpdate) {
        this.listUpdate = listUpdate;
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

    public Item adapter(ArrayAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public Item startActivityForResult(Intent intent, int requestCode) {
        this.intent = intent;
        this.requestCode = requestCode;
        return this;
    }

    public Item condition(TestInterface.Test test) {
        this.condition = test;
        return this;
    }

    private void create() {

    }

    public void add(ListActivity activity) {

        AlertInterface.Return aReturn = condition != null ? condition.onTest() ? update : elseUpdate != null ? elseUpdate : () -> "" : update;

        if (listUpdate != null && alert != null && adapter != null) {
            activity.addItemTitle(title);
            activity.addItemsAdapter(adapter, listUpdate, position -> {
                alert.onResult(position);
                if (intent != null)
                    startActivityForResult(intent, requestCode);
            });
        } else if (aReturn != null && alert != null) {
            activity.addItem(title, aReturn, s -> {
                alert.onResult(s);
                if (intent != null)
                    startActivityForResult(intent, requestCode);
            }, test);
        } else if (aReturn != null && voidAlert != null) {
            activity.addItem(title, aReturn, voidAlert);
        } else if (aReturn != null) {
            activity.addItem(title, aReturn, null, test);
        } else {
            activity.addItemTitle(title);
        }
    }
}
