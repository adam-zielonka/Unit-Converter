package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.tools.Theme;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void onClickOK(@SuppressWarnings("unused") View view) {
        finish();
    }

    public void onClickWebsite(@SuppressWarnings("unused") View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://adamzielonka.pro/"));
        startActivity(browserIntent);
    }
}