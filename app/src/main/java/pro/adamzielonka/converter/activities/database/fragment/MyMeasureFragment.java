package pro.adamzielonka.converter.activities.database.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyMeasureFragment extends MeasureListFragment {

    public MyMeasureFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference, String searchText) {
        if (getUid() == null) return null;
        return databaseReference.child("user-measures").child(getUid());
    }
}
