package pro.adamzielonka.converter.tools;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;
import java.util.Map;

import pro.adamzielonka.converter.R;

public class Language {
    public static String getLanguageWords(Map<String, String> map, String langCode, String globalCode) {
        return map.containsKey(langCode) ? map.get(langCode) : (map.containsKey(globalCode) ? map.get(globalCode) : "");
    }

    public static String getLangCode(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static String[] getLanguages(Context context) {
        return context.getResources().getStringArray(R.array.pref_theme_list_languages);
    }

    public static int getLanguageID(Context context) {
        String[] languages = getLanguages(context);
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(getLangCode(context))) return i;
        }
        return 0;
    }

    public static String getLanguageFromID(Context context, int languageID) {
        String[] languages = getLanguages(context);
        if (languageID >= languages.length)
            return languages[0];
        else
            return languages[languageID];
    }

    public static void setLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }
}
