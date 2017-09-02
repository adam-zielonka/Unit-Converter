package pro.adamzielonka.converter.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileTools {

    public static Gson getGson() {
        return new GsonBuilder().serializeSpecialFloatingPointValues().create();
    }
}
