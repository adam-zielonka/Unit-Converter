package pro.adamzielonka.converter.activities.cloud;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.adapters.CloudMeasureAdapter;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.cloud.CloudMeasure;

public class CloudListActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    ListView listView;
    CloudMeasureAdapter adapter;
    List<CloudMeasure> cloudMeasures = new ArrayList<>();
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();

        listView = findViewById(R.id.cloudListView);
        listView.setOnItemClickListener(this);
        listView.setTextFilterEnabled(true);
        adapter = new CloudMeasureAdapter(listView.getContext(), cloudMeasures);
        listView.setAdapter(adapter);
        loadMeasures();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void loadMeasures() {
        adapter.clear();
        adapter.notifyDataSetChanged();

        Query myMeasuresQuery = mDatabaseReference.child("converter").child("measures");
        myMeasuresQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                CloudMeasure cloudMeasure = dataSnapshot.getValue(CloudMeasure.class);
                cloudMeasures.add(cloudMeasure);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                for (ListIterator<CloudMeasure> iterator = cloudMeasures.listIterator(); iterator.hasNext(); ) {
                    CloudMeasure cloudMeasure = iterator.next();
                    if (cloudMeasure.getId().equals(dataSnapshot.getKey())) {
                        iterator.set(dataSnapshot.getValue(CloudMeasure.class));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (ListIterator<CloudMeasure> iterator = cloudMeasures.listIterator(); iterator.hasNext(); ) {
                    CloudMeasure cloudMeasure = iterator.next();
                    if (cloudMeasure.getId().equals(dataSnapshot.getKey())) {
                        iterator.remove();
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
