package pro.adamzielonka.converter.activities.database.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<CloudMeasure, MeasureViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public PostListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        if (postsQuery != null) {
            mAdapter = new FirebaseRecyclerAdapter<CloudMeasure, MeasureViewHolder>(CloudMeasure.class, R.layout.item_measure,
                    MeasureViewHolder.class, postsQuery) {
                @Override
                protected void populateViewHolder(final MeasureViewHolder viewHolder, final CloudMeasure model, final int position) {
                    final DatabaseReference postRef = getRef(position);

                    // Set click listener for the whole post view
                    final String postKey = postRef.getKey();
                    viewHolder.itemView.setOnClickListener(v -> {
                        // Launch MeasureDetailActivity
                        Intent intent = new Intent(getActivity(), MeasureDetailActivity.class);
                        intent.putExtra(MeasureDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    });
                    if (getUid() != null) {
                        // Determine if the current user has liked this post and set UI accordingly
                        if (model.stars.containsKey(getUid())) {
                            viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                        } else {
                            viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                        }

                        // Bind CloudMeasure to ViewHolder, setting OnClickListener for the star button

                        viewHolder.bindToPost(model, starView -> {
                            // Need to write to both places the post is stored
                            DatabaseReference globalPostRef = mDatabase.child("measures").child(postRef.getKey());
                            DatabaseReference userPostRef = mDatabase.child("user-measures").child(model.uid).child(postRef.getKey());

                            // Run two transactions
                            onStarClicked(globalPostRef);
                            onStarClicked(userPostRef);
                        });
                    } else {
                        viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                        viewHolder.bindToPost(model, starView -> {
                            // Need to write to both places the post is stored
                            DatabaseReference globalPostRef = mDatabase.child("measures").child(postRef.getKey());
                            DatabaseReference userPostRef = mDatabase.child("user-measures").child(model.uid).child(postRef.getKey());
                        });
                    }
                }
            };
            mRecycler.setAdapter(mAdapter);
        }
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CloudMeasure p = mutableData.getValue(CloudMeasure.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

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

    public abstract Query getQuery(DatabaseReference databaseReference);

}
