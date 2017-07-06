package pro.adamzielonka.converter.tools;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import pro.adamzielonka.converter.R;

public class Message {
    private static void showMessage(Activity activity, int msg, int color) {
        View parentLayout = activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG).setAction("Action", null);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(activity, color));
        snackbar.show();
    }

    public static void showError(Activity activity, int msg) {
        showMessage(activity, msg, R.color.colorRedPrimary);
    }

    public static void showSuccess(Activity activity, int msg) {
        showMessage(activity, msg, R.color.colorGreenPrimary);
    }
}
