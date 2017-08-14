package pro.adamzielonka.converter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

import pro.adamzielonka.converter.tools.LanguageMap;

abstract class MyAdapter<T> extends ArrayAdapter<T> {

    private String langCode;
    private String globalCode;

    MyAdapter(String langCode, String globalCode, @NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        this.langCode = langCode;
        this.globalCode = globalCode;
    }

    String getLanguageWords(LanguageMap map) {
        return map.get(langCode, globalCode);
    }
}
