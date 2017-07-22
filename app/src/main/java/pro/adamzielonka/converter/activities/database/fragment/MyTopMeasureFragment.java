package pro.adamzielonka.converter.activities.database.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopMeasureFragment extends MeasureListFragment {

    public MyTopMeasureFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference, String searchText) {
        if (getUid() == null) return null;
        return databaseReference.child("user-measures").child(getUid()).orderByChild("starCount");
    }
}
