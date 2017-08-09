package pro.adamzielonka.converter.activities.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.BaseActivity;
import pro.adamzielonka.converter.models.database.DataBaseMeasure;
import pro.adamzielonka.converter.services.MyDownloadService;

public class MeasureDetailActivity extends BaseActivity {

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mMeasureReference;
    private ValueEventListener mMeasureListener;
    private String mMeasureKey;

    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mVersionView;
    private TextView mBodyView;

    private DataBaseMeasure dataBaseMeasure;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgressDialog();

                switch (intent.getAction()) {
                    case MyDownloadService.DOWNLOAD_COMPLETED:
                        long numBytes = intent.getLongExtra(MyDownloadService.EXTRA_BYTES_DOWNLOADED, 0);
                        showMessageDialog(getString(R.string.success), String.format(Locale.getDefault(),
                                "%d bytes downloaded from %s",
                                numBytes,
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case MyDownloadService.DOWNLOAD_ERROR:
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                                "Failed to download from %s",
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                }
            }
        };

        mMeasureKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mMeasureKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mMeasureReference = FirebaseDatabase.getInstance().getReference()
                .child("measures").child(mMeasureKey);

        mAuthorView = findViewById(R.id.post_author);
        mTitleView = findViewById(R.id.post_title);
        mVersionView = findViewById(R.id.post_version);
        mBodyView = findViewById(R.id.post_body);
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataBaseMeasure = dataSnapshot.getValue(DataBaseMeasure.class);

                mAuthorView.setText(dataBaseMeasure.author);
                mTitleView.setText(dataBaseMeasure.title);
                mVersionView.setText(String.format("  v.%s", dataBaseMeasure.version));
                mBodyView.setText(dataBaseMeasure.units_symbols);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MeasureDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mMeasureReference.addValueEventListener(postListener);

        mMeasureListener = postListener;

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyDownloadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mMeasureListener != null) {
            mMeasureReference.removeEventListener(mMeasureListener);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_download) {
            beginDownload();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void beginDownload() {
        String path = "users/" + dataBaseMeasure.uid + "/" + dataBaseMeasure.file;

        Intent intent = new Intent(this, MyDownloadService.class)
                .putExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH, path)
                .setAction(MyDownloadService.ACTION_DOWNLOAD);
        startService(intent);

        showProgressDialog(getString(R.string.progress_downloading));
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }
}
