package pro.adamzielonka.converter.activities.database.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentMeasuresFragment extends MeasureListFragment {

    public RecentMeasuresFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference, String searchText) {
        if (searchText.equals(""))
            return databaseReference.child("measures").limitToFirst(100);
        else
            return databaseReference.child("measures")
                    .orderByChild("title_small")
                    .startAt(searchText.toLowerCase())
                    .endAt(searchText.toLowerCase() + "\uf8ff");
    }
}
