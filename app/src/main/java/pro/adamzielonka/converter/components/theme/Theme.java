package pro.adamzielonka.converter.components.theme;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.names.Property;

public class Theme {
    private Resources resources;
    private SharedPreferences preferences;

    public Theme(Activity activity) {
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        resources = activity.getResources();
        activity.setTheme(getStyleID());
    }

    public void setID(int position) {
        preferences.edit().putInt(Property.THEME, position).apply();
    }

    public int getID() {
        return preferences.getInt(Property.THEME, 0);
    }

    public int getTextColor() {
        return resources.getColor(getTextColorID());
    }

    public String[] getArray() {
        return resources.getStringArray(R.array.pref_list_themes);
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
