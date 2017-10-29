package pro.adamzielonka.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileTools {

    static Gson getGson() {
        return new GsonBuilder().serializeSpecialFloatingPointValues().create();
    }

    public static String toJSON(Object src) {
        return getGson().toJson(src);
    }
}
