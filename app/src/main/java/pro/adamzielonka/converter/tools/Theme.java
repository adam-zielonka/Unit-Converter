package pro.adamzielonka.converter.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pro.adamzielonka.converter.R;

public class Theme {
    public static int getStyleID(String themeID) {
        switch (themeID) {
            case "1":
                return R.style.RedTheme;
            case "2":
                return R.style.GreenTheme;
            case "3":
                return R.style.GreyTheme;
            default:
                return R.style.BlueTheme;
        }
    }

    public static int getConverterStyleID(String themeID) {
        switch (themeID) {
            case "1":
                return R.style.RedTheme_Converter;
            case "2":
                return R.style.GreenTheme_Converter;
            case "3":
                return R.style.GreyTheme_Converter;
            default:
                return R.style.BlueTheme_Converter;
        }
    }

    public static int getTextColorID(String themeID) {
        switch (themeID) {
            case "1":
                return R.color.colorRedPrimaryDark;
            case "2":
                return R.color.colorGreenPrimaryDark;
            case "3":
                return R.color.colorGreyPrimaryDark;
            default:
                return R.color.colorPrimaryDark;
        }
    }

    public static Integer getThemeID(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        return Integer.parseInt(preferences.getString("theme", "0"));
    }

    public static String getThemeName(Activity activity) {
        return getThemeNameFromID(activity, getThemeID(activity).toString());
    }

    public static String[] getThemes(Context context) {
        return context.getResources().getStringArray(R.array.pref_theme_list_titles);
    }

    public static String getThemeNameFromID(Context context, String themeID) {
        String[] themesName = getThemes(context);
        String[] themesID = context.getResources().getStringArray(R.array.pref_theme_list_values);
        for (int i = 0; i < themesID.length; i++) {
            if (themesID[i].equals(themeID)) return themesName[i];
        }
        return themesName[0];
    }
}
