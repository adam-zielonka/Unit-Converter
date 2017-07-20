package pro.adamzielonka.converter.activities.database.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    public MyPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        if(getUid() == null) return null;
        return databaseReference.child("user-measures")
                .child(getUid());
    }
}
