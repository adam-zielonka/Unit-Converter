package pro.adamzielonka.converter.activities.abstractes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;

import pro.adamzielonka.converter.components.MyProgressDialog;
import pro.adamzielonka.converter.components.theme.Theme;
import pro.adamzielonka.converter.database.UserAuth;

public abstract class BaseActivity extends AppCompatActivity {

    private int resultCode;
    private MyProgressDialog myProgressDialog;
    protected Theme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = new Theme(this);
        super.onCreate(savedInstanceState);
        myProgressDialog = new MyProgressDialog(this);

        resultCode = RESULT_CANCELED;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public void onBackPressed() {
        setResult(resultCode);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //region progress dialog
    public void showProgressDialog(String caption) {
        myProgressDialog.show(caption);
    }

    public void hideProgressDialog() {
        myProgressDialog.hide();
    }
    //endregion

    //region user
    public static String getUid() {
        return UserAuth.getUid();
    }

    public static FirebaseUser getUser() {
        return UserAuth.getUser();
    }
    //endregion

    //region start activity
    public void startWebsite(@StringRes int url) {
        startWebsite(getString(url));
    }

    public void startWebsite(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void startActivity(Class<?> cls) {
        startActivity(new Intent(getApplicationContext(), cls));
    }
    //endregion

}
