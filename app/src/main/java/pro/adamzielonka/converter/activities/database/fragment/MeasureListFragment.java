package pro.adamzielonka.converter.activities.database.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.database.MeasureDetailActivity;
import pro.adamzielonka.converter.activities.database.viewholder.MeasureViewHolder;
import pro.adamzielonka.converter.models.database.CloudMeasure;

public abstract class MeasureListFragment extends Fragment {

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<CloudMeasure, MeasureViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private EditText editSearch;
    private ImageButton buttonSearch;
    private LinearLayoutManager mManager;

    public MeasureListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);
        editSearch = rootView.findViewById(R.id.editSearch);
        buttonSearch = rootView.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(view -> onClickSearch());

        return rootView;
    }

    public void onClickSearch() {
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
        onQuery(editSearch.getText().toString());
    }

    public void onQuery(String searchText) {
        Query postsQuery = getQuery(mDatabase, searchText);
        if (postsQuery != null) {
            mAdapter = new FirebaseRecyclerAdapter<CloudMeasure, MeasureViewHolder>(CloudMeasure.class, R.layout.item_measure,
                    MeasureViewHolder.class, postsQuery) {
                @Override
                protected void populateViewHolder(final MeasureViewHolder viewHolder, final CloudMeasure model, final int position) {
                    final DatabaseReference postRef = getRef(position);

                    final String postKey = postRef.getKey();
                    viewHolder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity(), MeasureDetailActivity.class);
                        intent.putExtra(MeasureDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    });
                    if (getUid() != null) {
                        if (model.stars.containsKey(getUid())) {
                            viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                        } else {
                            viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                        }

                        viewHolder.bindToPost(model, starView -> {
                            DatabaseReference globalPostRef = mDatabase.child("measures").child(postRef.getKey());
                            DatabaseReference userPostRef = mDatabase.child("user-measures").child(model.uid).child(postRef.getKey());

                            onStarClicked(globalPostRef);
                            onStarClicked(userPostRef);
                        });
                    } else {
                        viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                        viewHolder.bindToPost(model, null);
                    }
                }
            };
            mRecycler.setAdapter(mAdapter);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        onQuery("");
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CloudMeasure p = mutableData.getValue(CloudMeasure.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return getUser() != null ? getUser().getUid() : null;
    }

    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public abstract Query getQuery(DatabaseReference databaseReference, String searchText);

}
