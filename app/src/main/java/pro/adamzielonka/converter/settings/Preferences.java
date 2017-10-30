package pro.adamzielonka.converter.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    public static SharedPreferences getPreferences(Activity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
    }

    public static void setPreferences(Activity activity, String name, String value) {
        SharedPreferences.Editor editor = getPreferences(activity).edit();
        editor.putString(name, value + "");
        editor.apply();
    }

    public static void setPreferences(Activity activity, String name, int value) {
        SharedPreferences.Editor editor = getPreferences(activity).edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public static int getPreferences(Activity activity, String name, int defValue) {
        return getPreferences(activity).getInt(name, defValue);
    }

    public static String getPreferences(Activity activity, String name, String defValue) {
        return getPreferences(activity).getString(name, defValue);
    }
}
