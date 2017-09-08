package pro.adamzielonka.converter.components;

import android.app.Activity;
import android.app.ProgressDialog;

import pro.adamzielonka.converter.R;

public class MyProgressDialog {

    private ProgressDialog mProgressDialog;
    private Activity activity;

    public MyProgressDialog(Activity activity){
        this.activity = activity;
    }

    public void show(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption != null ? caption : activity.getString(R.string.loading));
        mProgressDialog.show();
    }

    public void show() {
        show(null);
    }

    public void hide() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
