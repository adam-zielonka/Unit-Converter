package pro.adamzielonka.converter.activities.abstractes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import pro.adamzielonka.converter.components.theme.Theme;

public abstract class BaseActivity extends AppCompatActivity {

    private int resultCode;
    protected Theme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = new Theme(this);
        super.onCreate(savedInstanceState);

        resultCode = RESULT_CANCELED;
    }

    public void setResultOK() {
        this.resultCode = RESULT_OK;
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

    //region start activity
    public void startWebsite(@StringRes int url) {
        startWebsite(getString(url));
    }

    public void startWebsite(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
    //endregion

}
