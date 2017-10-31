package pro.adamzielonka.converter.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.names.Property;

public class Language implements SettingInterface {

    private SharedPreferences preferences;
    private Activity activity;

    public Language(Activity activity) {
        preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        this.activity = activity;
        check();
    }

    private void check() {
        String lang = preferences.getString(Property.LANGUAGE, "");
        setLanguage(activity, lang.isEmpty() ? getOSLanguage(activity) : lang);
    }

    @Override
    public String get() {
        String lang = preferences.getString(Property.LANGUAGE, "");
        return lang.isEmpty() ? activity.getString(R.string.language_os) : getLanguage(lang);
    }

    @Override
    public String[] getArray() {
        String[] languages = getDisplayLanguages(activity);
        languages[0] = activity.getString(R.string.language_os);
        return languages;
    }

    @Override
    public int getID() {
        String lang = preferences.getString(Property.LANGUAGE, "");
        return lang.isEmpty() ? 0 : getLanguageID(activity);
    }

    @Override
    public void setID(Integer id) {
        String lang = getLanguageFromID(activity, id);
        setLanguage(activity, id != 0 ? lang : getOSLanguage(activity));
        preferences.edit().putString(Property.LANGUAGE, lang).apply();
    }

    public static void setOSLanguage(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        preferences.edit().putString(Property.LANGUAGE_OS, getLangCode(activity)).apply();
    }

    public static String getOSLanguage(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        return preferences.getString(Property.LANGUAGE_OS, "en");
    }

    public static void setConverterLanguage(Activity activity, String lang) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        preferences.edit().putString(Property.LANGUAGE_CONVERTER, lang).apply();
    }

    public static String getConverterLanguage(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        String lang = preferences.getString(Property.LANGUAGE_CONVERTER, "");
        return lang.isEmpty() ? activity.getString(R.string.language_os) : getLanguage(lang);
    }

    public static String getConverterLanguageCode(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        String lang = preferences.getString(Property.LANGUAGE_CONVERTER, "");
        return lang.isEmpty() ? getOSLanguage(activity) : lang;
    }

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
