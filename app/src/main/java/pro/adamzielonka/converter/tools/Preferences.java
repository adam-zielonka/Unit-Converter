package pro.adamzielonka.converter.tools;

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
}
