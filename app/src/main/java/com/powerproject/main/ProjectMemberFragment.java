package com.powerproject.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.powerproject.login.R;

public abstract class ProjectMemberFragment extends Fragment {

    private static final String TAG = "PostListFragment";
    public static String edNameProject, edDescProject, edStart, edFinish, edStatusProject, idproject;
    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    public static DatabaseReference myRef;
    public static DatabaseReference postRef1;

    private FirebaseRecyclerAdapter<ProjectMember, ProjectMemberViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public ProjectMemberFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

         // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.project_list);
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
        mAdapter = new FirebaseRecyclerAdapter<ProjectMember, ProjectMemberViewHolder> (ProjectMember.class, R.layout.layout_project_member,
                ProjectMemberViewHolder.class,  postsQuery) {
            @Override
            protected void populateViewHolder(final ProjectMemberViewHolder projectViewHolder, final ProjectMember project, int i) {
                final DatabaseReference postRef = getRef(i);
                postRef1 = postRef;

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                projectViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //statement if click
                    }
                });

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                projectViewHolder.bindToPost(project, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Need to write to both places the post is stored
                        final DatabaseReference globalPostRef = mDatabase.child("project").child(postRef.getKey());

                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}

