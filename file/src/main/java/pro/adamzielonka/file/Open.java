package pro.adamzielonka.file;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Open {

    public static <T> T openJSON(InputStream in, Class<T> classOfT) throws FileNotFoundException {
        Reader reader = new BufferedReader(new InputStreamReader(in));
        return FileTools.getGson().fromJson(reader, classOfT);
    }

    public static <T> T openJSON(Context context, Uri uri, Class<T> classOfT) throws FileNotFoundException {
        return openJSON(context.getContentResolver().openInputStream(uri), classOfT);
    }

    public static <T> T openJSON(Context context, String fileName, Class<T> classOfT) throws FileNotFoundException {
        return openJSON(context.openFileInput(fileName), classOfT);
    }

    public static <T> List<T> openJSONs(Context context, String filePrefix, Class<T> classOfT) {
        File[] files = context.getFilesDir().listFiles();

        List<T> arrayOfT = new ArrayList<>();
        for (File file : files)
            if (file.getName().contains(filePrefix)) try {
                arrayOfT.add(openJSON(context, file.getName(), classOfT));
            } catch (Exception e) {
                e.printStackTrace();
            }

        return arrayOfT;
    }
}
