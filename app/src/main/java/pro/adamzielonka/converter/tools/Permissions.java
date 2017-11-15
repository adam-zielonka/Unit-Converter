package pro.adamzielonka.converter.tools;

import android.Manifest;

public class Permissions {
    public static String[] getReadAndWritePermissionsStorage() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }
}
