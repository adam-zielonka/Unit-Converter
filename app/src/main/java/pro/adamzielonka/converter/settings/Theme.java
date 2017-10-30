package pro.adamzielonka.converter.settings;

import android.app.Activity;
import android.content.res.Resources;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.names.Property;

public class Theme implements SettingInterface {
    private Resources resources;
    private Activity activity;

    public Theme(Activity activity) {
        resources = activity.getResources();
        activity.setTheme(getStyleID());
        this.activity = activity;
    }

    public void setID(Integer position) {
        Preferences.setPreferences(activity, Property.THEME, position);
    }

    public int getID() {
        return Preferences.getPreferences(activity, Property.THEME, 0);
    }

    public int getTextColor() {
        return resources.getColor(getTextColorID());
    }

    public String[] getArray() {
        return resources.getStringArray(R.array.pref_list_themes);
    }

    public String get() {
        return get(getID());
    }

    private String get(int themeID) {
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
