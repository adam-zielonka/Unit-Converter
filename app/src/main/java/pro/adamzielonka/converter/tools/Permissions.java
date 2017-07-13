package pro.adamzielonka.converter.tools;

import android.Manifest;

public class Permissions {
    public static String[] getReadAndWritePermissionsStorage() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN
                ? new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                : new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }
}
