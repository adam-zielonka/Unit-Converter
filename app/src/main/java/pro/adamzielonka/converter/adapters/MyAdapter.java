package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Map;

import pro.adamzielonka.converter.tools.Language;

abstract class MyAdapter<T> extends ArrayAdapter<T> {

    public String langCode;
    public String globalCode;

    MyAdapter(String langCode, String globalCode, @NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        this.langCode = langCode;
        this.globalCode = globalCode;
    }

    String getLanguageWords(Map<String, String> map) {
        return Language.getLanguageWords(map, langCode, globalCode);
    }
}
