package pro.adamzielonka.converter.file;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static pro.adamzielonka.converter.file.FileTools.getGson;

public class Open {

    //region internal
    public static <T> T openJSON(Context context, String fileName, Class<T> classOfT) throws FileNotFoundException {
        FileInputStream in = context.openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        return getGson().fromJson(reader, classOfT);
    }

    public static <T> List<T> openJSONs(Context context, String filePrefix, Class<T> classOfT) {
        File[] files = context.getFilesDir().listFiles();

        List<T> arrayList = new ArrayList<>();
        for (File file : files)
            if (file.getName().contains(filePrefix)) try {
                arrayList.add(openJSON(context, file.getName(), classOfT));
            } catch (Exception e) {
                e.printStackTrace();
            }

        return arrayList;
    }
    //endregion

    //region external
    public static InputStream openFileToInputStream(Context context, Uri uri) throws FileNotFoundException {
        return context.getContentResolver().openInputStream(uri);
    }
    //endregion
}
