package pro.adamzielonka.items.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

public class DialogBuilder {
    protected Activity activity;
    protected String title;

    public DialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogBuilder setTitle(@StringRes int title) {
        setTitle(activity.getString(title));
        return this;
    }

    public DialogBuilder(@NonNull Activity activity) {
        this.activity = activity;
    }

    public AlertDialog.Builder create() {
        title = title != null ? title : "";
        return getAlert();
    }

    protected AlertDialog.Builder getAlert() {
        return new AlertDialog.Builder(activity)
                .setTitle(title)
                .setCancelable(true);
    }
}
