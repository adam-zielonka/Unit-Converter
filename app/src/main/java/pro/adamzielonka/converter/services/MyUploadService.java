package pro.adamzielonka.converter.services;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.SplashActivity;
import pro.adamzielonka.converter.names.Extra;

public class MyUploadService extends MyBaseTaskService {

    private static final String TAG = "MyUploadService";

    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_FILE_USER = "extra_file_user";
    public static final String EXTRA_FILE_CONCRETE = "extra_file_concrete";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

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
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);
        if (ACTION_UPLOAD.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(EXTRA_FILE_URI);
            String fileUser = intent.getStringExtra(EXTRA_FILE_USER);
            concreteFileName = intent.getStringExtra(EXTRA_FILE_CONCRETE);
            uploadFromUri(fileUri, fileUser);
        }

        return START_REDELIVER_INTENT;
    }

    private void uploadFromUri(final Uri fileUri, final String fileUser) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        taskStarted();
        showProgressNotification(getString(R.string.progress_uploading), 0, 0);

        final StorageReference photoRef = mStorageRef.child("users").child(fileUser)
                .child(fileUri.getLastPathSegment());

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/json")
                .build();

        photoRef.putFile(fileUri, metadata).
                addOnProgressListener(taskSnapshot -> showProgressNotification(getString(R.string.progress_uploading),
                        taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())
                )
                .addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getMetadata() != null) {
                        Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

                        broadcastUploadFinished(downloadUri, fileUri);
                        showUploadFinishedNotification(downloadUri, fileUri);
                    }
                    taskCompleted();
                })
                .addOnFailureListener(exception -> {
                    broadcastUploadFinished(null, fileUri);
                    showUploadFinishedNotification(null, fileUri);

                    taskCompleted();
                });
    }

    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;

        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        dismissProgressNotification();

        Intent intent = new Intent(this, SplashActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .putExtra(Extra.MEASURE_FILE_NAME, concreteFileName)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;
        String caption = success ? getString(R.string.upload_success) : getString(R.string.upload_failure);
        showFinishedNotification(caption, intent, success);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPLOAD_COMPLETED);
        filter.addAction(UPLOAD_ERROR);

        return filter;
    }

}
