package pro.adamzielonka.converter.activities.abstractes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import pro.adamzielonka.converter.activities.ConverterActivity;

public abstract class PreferenceActivity extends ListActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        super.onCreate(savedInstanceState);
        try {
            onUpdate();
        } catch (Exception e) {
            finish();
        }
    }

    protected void onUpdate() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ConverterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void setPreferences(String name, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value + "");
        editor.apply();
    }
}
