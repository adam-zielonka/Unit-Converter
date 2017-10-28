package pro.adamzielonka.converter.database;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.BaseActivity;
import pro.adamzielonka.converter.models.concrete.CMeasure;
import pro.adamzielonka.converter.models.database.CloudMeasure;
import pro.adamzielonka.converter.models.database.User;
import pro.adamzielonka.converter.models.file.Measure;
import pro.adamzielonka.converter.services.MyUploadService;

import static pro.adamzielonka.converter.database.FireBaseNames.MEASURES;
import static pro.adamzielonka.converter.database.FireBaseNames.USERS;
import static pro.adamzielonka.converter.database.UserAuth.getUid;
import static pro.adamzielonka.converter.tools.Language.getLangCode;

public class UploadMeasure {

    private DatabaseReference mDatabase;
    private BaseActivity activity;
    private Measure measure;
    private CMeasure cMeasure;
    private OnUploadResult onUploadResult;

    public UploadMeasure(BaseActivity activity, OnUploadResult onUploadResult) {
        this.activity = activity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.onUploadResult = onUploadResult;
    }

    public interface OnUploadResult {
        void onUploadResult();
    }

    public void submitMeasure(Measure measure, CMeasure cMeasure) {
        this.measure = measure;
        this.cMeasure = cMeasure;

        String userId = getUid();
        if (userId == null) return;
        mDatabase.child(USERS).child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            Toast.makeText(activity,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            updateMeasure(userId, user.name, measure.getName(getLangCode(activity)), cMeasure.getUnitsOrder());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateMeasure(String userId, String username, String title, String body) {
        if (measure.cloudID.equals("")) {
            String key = mDatabase.child("measures").push().getKey();
            CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
            doUpdateMeasure(key, cloudMeasure);
        } else {
            Query query = mDatabase.child("measures");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(measure.cloudID)) {
                        CloudMeasure cloudMeasure = snapshot.child(measure.cloudID).getValue(CloudMeasure.class);
                        if (cloudMeasure != null) {
                            if (cloudMeasure.uid.equals(getUid())) {
                                cloudMeasure.version++;
                                cloudMeasure.title = title;
                                cloudMeasure.units_symbols = body;
                                cloudMeasure.units_names = body;
                                doUpdateMeasure(measure.cloudID, cloudMeasure);
                            } else {
                                String key = mDatabase.child(MEASURES).push().getKey();
                                cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
                                doUpdateMeasure(key, cloudMeasure);
                            }
                        } else {
                            String key = mDatabase.child(MEASURES).push().getKey();
                            cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
                            doUpdateMeasure(key, cloudMeasure);
                        }
                    } else {
                        String key = mDatabase.child(MEASURES).push().getKey();
                        CloudMeasure cloudMeasure = new CloudMeasure(userId, username, title, body, body, 1L);
                        doUpdateMeasure(key, cloudMeasure);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    private void doUpdateMeasure(String key, CloudMeasure cloudMeasure) {
        cloudMeasure.file = cMeasure.userFileName;
        Map<String, Object> postValues = cloudMeasure.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/measures/" + key, postValues);
        childUpdates.put("/user-measures/" + cloudMeasure.uid + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        DatabaseReference ref = mDatabase.child(MEASURES).child(key).child("version");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long version = dataSnapshot.getValue(Long.class);
                measure.cloudID = key;
                measure.version = version;
                onUploadResult.onUploadResult();
                File file = new File(activity.getFilesDir() + "/" + cMeasure.userFileName);
                uploadFromUri(Uri.parse(file.toURI().toString()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void uploadFromUri(Uri fileUri) {
        activity.startService(new Intent(activity, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .putExtra(MyUploadService.EXTRA_FILE_USER, getUid())
                .putExtra(MyUploadService.EXTRA_FILE_CONCRETE, cMeasure.concreteFileName)
                .setAction(MyUploadService.ACTION_UPLOAD));

        activity.showProgressDialog(activity.getString(R.string.progress_uploading));
    }
}
