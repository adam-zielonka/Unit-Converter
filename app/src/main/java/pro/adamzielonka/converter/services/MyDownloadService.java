package pro.adamzielonka.converter.services;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.SplashActivity;
import pro.adamzielonka.converter.models.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.models.user.Measure;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;
import static pro.adamzielonka.converter.tools.Converter.getLangCode;
import static pro.adamzielonka.converter.tools.FileTools.getNewFileInternalName;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.openFileToInputStream;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;

public class MyDownloadService extends MyBaseTaskService {

    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String DOWNLOAD_COMPLETED = "download_completed";
    public static final String DOWNLOAD_ERROR = "download_error";

    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";

    private StorageReference mStorageRef;
    private String concreteFileName;

    @Override
    public void onCreate() {
        super.onCreate();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            String downloadPath = intent.getStringExtra(EXTRA_DOWNLOAD_PATH);
            downloadFromPath(downloadPath);
        }

        return START_REDELIVER_INTENT;
    }

    private void downloadFromPath(final String downloadPath) {
        taskStarted();
        showProgressNotification(getString(R.string.progress_downloading), 0, 0);

        try {
            File localFile = File.createTempFile("cloud_measure", "json");

            mStorageRef.child(downloadPath).getFile(localFile)
                    .addOnProgressListener(taskSnapshot -> showProgressNotification(getString(R.string.progress_downloading),
                            taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())
                    )
                    .addOnSuccessListener(taskSnapshot -> {
                        broadcastDownloadFinished(downloadPath, taskSnapshot.getTotalByteCount());
                        showDownloadFinishedNotification(downloadPath, (int) taskSnapshot.getTotalByteCount());

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileToInputStream(this, Uri.parse(localFile.toURI().toString()))));

                            Gson gson = getGson();
                            Measure userMeasure = gson.fromJson(reader, Measure.class);
                            ConcreteMeasure concreteMeasure = userMeasure.getConcreteMeasure();

                            concreteFileName = getNewFileInternalName(this, "concrete_", concreteMeasure.getName(getLangCode()));
                            String userFileName = getNewFileInternalName(this, "user_", concreteMeasure.getName(getLangCode()));

                            concreteMeasure.concreteFileName = concreteFileName;
                            concreteMeasure.userFileName = userFileName;

                            saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
                            saveToInternal(this, userFileName, gson.toJson(userMeasure));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        taskCompleted();
                    })
                    .addOnFailureListener(exception -> {
                        broadcastDownloadFinished(downloadPath, -1);
                        showDownloadFinishedNotification(downloadPath, -1);

                        taskCompleted();
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean broadcastDownloadFinished(String downloadPath, long bytesDownloaded) {
        boolean success = bytesDownloaded != -1;
        String action = success ? DOWNLOAD_COMPLETED : DOWNLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_PATH, downloadPath)
                .putExtra(EXTRA_BYTES_DOWNLOADED, bytesDownloaded);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    private void showDownloadFinishedNotification(String downloadPath, int bytesDownloaded) {
        dismissProgressNotification();

        Intent intent = new Intent(this, SplashActivity.class)
                .putExtra(EXTRA_DOWNLOAD_PATH, downloadPath)
                .putExtra(EXTRA_BYTES_DOWNLOADED, bytesDownloaded)
                .putExtra(EXTRA_MEASURE_FILE_NAME, concreteFileName)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = bytesDownloaded != -1;
        String caption = success ? getString(R.string.download_success) : getString(R.string.download_failure);
        showFinishedNotification(caption, intent, true);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DOWNLOAD_COMPLETED);
        filter.addAction(DOWNLOAD_ERROR);

        return filter;
    }
}
