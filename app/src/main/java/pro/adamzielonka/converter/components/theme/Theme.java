package pro.adamzielonka.converter.components.theme;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pro.adamzielonka.converter.R;

public class Theme {
    private Activity activity;
    private SharedPreferences preferences;

    public Theme(Activity activity) {
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        this.activity = activity;
        activity.setTheme(getStyleID());
    }

    public void setID(int position) {
        preferences.edit().putInt("theme", position).apply();
    }

    public int getTextColor() {
        return activity.getResources().getColor(getTextColorID());
    }

    public int getID() {
        return preferences.getInt("theme", 0);
    }

    public String[] getArray() {
        return activity.getResources().getStringArray(R.array.pref_theme_list_titles);
    }

    public String getName() {
        return getName(getID());
    }

    private String getName(int themeID) {
        String[] themes = getArray();
        if (themeID >= 0 && themeID < themes.length)
            return themes[themeID];
        return themes[0];
    }

    protected int getStyleID() {
        switch (getID()) {
            case 1:
                return R.style.RedTheme;
            case 2:
                return R.style.GreenTheme;
            case 3:
                return R.style.GreyTheme;
            default:
                return R.style.BlueTheme;
        }
    }

    private int getTextColorID() {
        switch (getID()) {
            case 1:
                return R.color.colorRedPrimaryDark;
            case 2:
                return R.color.colorGreenPrimaryDark;
            case 3:
                return R.color.colorGreyPrimaryDark;
            default:
                return R.color.colorPrimaryDark;
        }
    }
}
