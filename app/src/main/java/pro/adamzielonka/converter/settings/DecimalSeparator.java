package pro.adamzielonka.converter.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pro.adamzielonka.converter.names.Property;
import pro.adamzielonka.java.Number;

public class DecimalSeparator implements SettingInterface {

    private SharedPreferences preferences;

    public DecimalSeparator(Activity activity) {
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        check();
    }

    private void check() {
        if (preferences.getString(Property.DECIMAL_SEPARATOR, ".").equals("."))
            Number.setDotDecimalSeparator();
        else Number.setCommaDecimalSeparator();
    }

    public String get() {
        return Number.getDecimalSeparator();
    }

    public String[] getArray() {
        return new String[]{".", ","};
    }

    public int getID() {
        return Number.getDecimalSeparator().equals(".") ? 0 : 1;
    }

    public void setID(Integer id) {
        if (id == 0) Number.setDotDecimalSeparator();
        else Number.setCommaDecimalSeparator();
        preferences.edit().putString(Property.DECIMAL_SEPARATOR, get()).apply();
    }
}
