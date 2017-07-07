package pro.adamzielonka.converter.tools;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

import static android.content.Context.MODE_PRIVATE;

public class FileTools {
    private static boolean isFileInternalExist(Context context, String fileName) {
        return context.getFileStreamPath(fileName).exists();
    }

    public static String getFileInternalName(Context context, String prefix, String name) {
        String fileName = prefix + name.toUpperCase() + ".json";
        for (int i = 1; isFileInternalExist(context, fileName); i++) {
            fileName = prefix + name.toUpperCase() + "_" + i + ".json";
        }
        return fileName;
    }

    public static void saveToInternal(Context context, String fileName, String file) throws IOException {
        FileOutputStream out = context.openFileOutput(fileName, MODE_PRIVATE);
        out.write(file.getBytes());
        out.close();
    }

    public static InputStream openFileToInputStream(Context context, Uri uri) throws FileNotFoundException {
        return context.getContentResolver().openInputStream(uri);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static Uri getFileUri(String name) {
        String fileName = "converter_" + name.toLowerCase() + ".json";
        File file = new java.io.File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        for (int i = 1; file.exists(); i++) {
            fileName = "converter_" + name.toLowerCase() + "_" + i + ".json";
            file = new java.io.File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), fileName);
        }
        return Uri.parse(file.toURI().toString());
    }

    public static ConcreteMeasure openConcreteMeasure(Context context, String fileName) throws FileNotFoundException {
        FileInputStream in = context.openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        return gson.fromJson(reader, ConcreteMeasure.class);
    }

    public static Measure openMeasure(Context context, String fileName) throws FileNotFoundException {
        FileInputStream in = context.openFileInput(fileName);
        Reader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        return gson.fromJson(reader, Measure.class);
    }
}
