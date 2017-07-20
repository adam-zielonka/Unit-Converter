package pro.adamzielonka.converter.services;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

import static pro.adamzielonka.converter.tools.FileTools.getFileInternalName;
import static pro.adamzielonka.converter.tools.FileTools.getGson;
import static pro.adamzielonka.converter.tools.FileTools.openFileToInputStream;
import static pro.adamzielonka.converter.tools.FileTools.saveToInternal;

/**
 * Service to handle downloading files from Firebase Storage.
 */
public class MyDownloadService extends MyBaseTaskService {

    private static final String TAG = "Storage#DownloadService";

    /**
     * Actions
     **/
    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String DOWNLOAD_COMPLETED = "download_completed";
    public static final String DOWNLOAD_ERROR = "download_error";

    /**
     * Extras
     **/
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";

    private StorageReference mStorageRef;
    private String concreteFileName;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);

        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            // Get the path to download from the intent
            String downloadPath = intent.getStringExtra(EXTRA_DOWNLOAD_PATH);
            downloadFromPath(downloadPath);
        }

        return START_REDELIVER_INTENT;
    }

    private void downloadFromPath(final String downloadPath) {
        Log.d(TAG, "downloadFromPath:" + downloadPath);

        // Mark task started
        taskStarted();
        showProgressNotification(getString(R.string.progress_downloading), 0, 0);

        try {
            File localFile = File.createTempFile("cloud_measure", "json");

            // Download and get total bytes
            mStorageRef.child(downloadPath).getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "download:SUCCESS");

                        // Send success broadcast with number of bytes downloaded
                        broadcastDownloadFinished(downloadPath, taskSnapshot.getTotalByteCount());
                        showDownloadFinishedNotification(downloadPath, (int) taskSnapshot.getTotalByteCount());

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileToInputStream(this, Uri.parse(localFile.toURI().toString()))));

                            Gson gson = getGson();
                            Measure userMeasure = gson.fromJson(reader, Measure.class);
                            ConcreteMeasure concreteMeasure = userMeasure.getConcreteMeasure();

                            String concreteFileName = getFileInternalName(this, "concrete_", concreteMeasure.getName());
                            String userFileName = getFileInternalName(this, "user_", concreteMeasure.getName());

                            concreteMeasure.setConcreteFileName(concreteFileName);
                            concreteMeasure.setUserFileName(userFileName);

                            saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
                            saveToInternal(this, userFileName, gson.toJson(userMeasure));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Mark task completed
                        taskCompleted();
                    })
                    .addOnFailureListener(exception -> {
                        Log.w(TAG, "download:FAILURE", exception);

                        // Send failure broadcast
                        broadcastDownloadFinished(downloadPath, -1);
                        showDownloadFinishedNotification(downloadPath, -1);

                        // Mark task completed
                        taskCompleted();
                    }).addOnProgressListener(taskSnapshot -> showProgressNotification(getString(R.string.progress_downloading), taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcast finished download (success or failure).
     *
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastDownloadFinished(String downloadPath, long bytesDownloaded) {
        boolean success = bytesDownloaded != -1;
        String action = success ? DOWNLOAD_COMPLETED : DOWNLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_PATH, downloadPath)
                .putExtra(EXTRA_BYTES_DOWNLOADED, bytesDownloaded);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    /**
     * Show a notification for a finished download.
     */
    private void showDownloadFinishedNotification(String downloadPath, int bytesDownloaded) {
        // Hide the progress notification
        dismissProgressNotification();

        Intent intent = new Intent(this, SplashActivity.class)
                .putExtra(EXTRA_DOWNLOAD_PATH, downloadPath)
                .putExtra(EXTRA_BYTES_DOWNLOADED, bytesDownloaded)
                .putExtra("measureFileName", concreteFileName)
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
