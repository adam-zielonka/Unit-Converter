package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.PreferenceActivity;
import pro.adamzielonka.converter.activities.database.GoogleSignInActivity;

import static pro.adamzielonka.converter.tools.Theme.getThemeID;
import static pro.adamzielonka.converter.tools.Theme.getThemeName;
import static pro.adamzielonka.converter.tools.Theme.getThemes;

public class SettingsActivity extends PreferenceActivity implements ListView.OnItemClickListener {

    private View themeView;
    private View logInView;
    SharedPreferences preferences;

    @Override
    public void onLoad() throws Exception {
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.pref_header_appearance));
        themeView = listView.addHeaderItem(getString(R.string.pref_title_theme));
        listView.addHeaderTitle(getString(R.string.pref_header_user));
        logInView = listView.addHeaderItem(getString(R.string.login_label));
    }

    public void onUpdate() {
        updateView(themeView, getThemeName(this));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(themeView)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.pref_title_theme)
                    .setSingleChoiceItems(getThemes(this), getThemeID(this), (dialogInterface, i) -> {
                        int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("theme", selectedPosition + "");
                        editor.apply();
                        dialogInterface.dismiss();
                        onUpdate();
                    })
                    .setCancelable(true)
                    .show();
        } else if (view.equals(logInView)) {
            Intent intent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("theme")) {
            Intent settings = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(settings);
            overridePendingTransition(0, 0);
            finish();
        }
    }

}
