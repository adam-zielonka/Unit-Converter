package pro.adamzielonka.converter.components;

import android.app.Activity;
import android.app.ProgressDialog;

public class MyProgressDialog {

    private ProgressDialog mProgressDialog;
    private Activity activity;

    public MyProgressDialog(Activity activity){
        this.activity = activity;
    }

    public void show(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new android.app.ProgressDialog(activity);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    public void show() {
        if (mProgressDialog == null) {
            mProgressDialog = new android.app.ProgressDialog(activity);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hide() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
