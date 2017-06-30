package pro.adamzielonka.converter.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import pro.adamzielonka.converter.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
    }
}