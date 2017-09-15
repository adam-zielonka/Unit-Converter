package pro.adamzielonka.converter.tools;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;
import java.util.Map;

import pro.adamzielonka.converter.R;

public class Language {
    public static String getLanguageWords(Map<String, String> map, String globalCode) {
        return (map.containsKey(globalCode) ? map.get(globalCode) : "");
    }

    public static String getLangCode(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static String getLanguage(String langCode) {
        Locale locale = new Locale(langCode);
        return locale.getDisplayLanguage();
    }

    public static String getLanguage(String langCode, String langConverter) {
        Locale locale = new Locale(langCode);
        return locale.getDisplayLanguage(new Locale(langConverter));
    }

    private static String[] getLanguages(Context context) {
        return context.getResources().getStringArray(R.array.pref_list_languages);
    }

    public static String[] getDisplayLanguages(Context context) {
        String[] languagesCode = getLanguages(context);
        String[] languages = new String[languagesCode.length];
        for (int i = 0; i < languagesCode.length; i++) {
            languages[i] = getLanguage(languagesCode[i]);
        }
        return languages;
    }

    public static String getDisplayLanguage(Context context) {
        return getLanguage(getLangCode(context));
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
