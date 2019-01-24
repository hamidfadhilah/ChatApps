package com.powerproject.main;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.powerproject.login.R;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class ProjectFragment extends Fragment {
    private static final String TAG = "PostListFragment";
    public static String edNameProject, edDescProject, edStart, edFinish, edStatusProject, idproject;
    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    public static DatabaseReference myRef;
    public static DatabaseReference postRef1;

    private FirebaseRecyclerAdapter<Project, ProjectViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public ProjectFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

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
        mAdapter = new FirebaseRecyclerAdapter<Project, ProjectViewHolder> (Project.class, R.layout.layout_project,
                ProjectViewHolder.class,  postsQuery) {
            @Override
            protected void populateViewHolder(final ProjectViewHolder projectViewHolder, final Project project, int i) {
                final DatabaseReference postRef = getRef(i);
                postRef1 = postRef;

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                projectViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ProjectDetailActivity.class));
                        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase database= FirebaseDatabase.getInstance();
                        myRef = database.getReference().child("project").child(userId).child(postRef.getKey());
                        myRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Project project = dataSnapshot.getValue(Project.class);

                                        idproject = postRef.getKey();
                                        edNameProject = project.projectname;
                                        edStatusProject = project.status;
                                        edStart = project.projectstart;
                                        edFinish = project.projectfinish;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                projectViewHolder.bindToPost(project, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Need to write to both places the post is stored
                        final DatabaseReference globalPostRef = mDatabase.child("project").child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        if (v == projectViewHolder.delete){
                            AlertDialog.Builder adb=new AlertDialog.Builder(v.getContext());
                            adb.setTitle("Delete?");
                            adb.setMessage("Are you sure you want to delete this project?");
                            adb.setNegativeButton("Cancel", null);
                            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    mDatabase.child("project").child(userId).child(postRef.getKey()).removeValue();
                                    //delete table task member
                                    mDatabase.child("task_member").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot)
                                        {
                                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                            {
                                                mDatabase.child("task_member").child(postSnapshot.getKey()).orderByChild("id_project").equalTo(postRef.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot ds)
                                                    {
                                                        for (DataSnapshot ps : ds.getChildren())
                                                        {
                                                            mDatabase.child("task_member").child(postSnapshot.getKey()).child(ps.getKey()).removeValue();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    //delete project member
                                    mDatabase.child("project_member").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot)
                                        {
                                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                            {
                                                mDatabase.child("project_member").child(postSnapshot.getKey()).orderByChild("id_project").equalTo(postRef.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot ds)
                                                    {
                                                        for (DataSnapshot ps : ds.getChildren())
                                                        {
                                                            mDatabase.child("project_member").child(postSnapshot.getKey()).child(ps.getKey()).removeValue();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    //delete project_owner
                                    mDatabase.child("project_owner").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot)
                                        {
                                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                            {
                                                mDatabase.child("project_owner").child(postSnapshot.getKey()).orderByChild("id_project").equalTo(postRef.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot ds)
                                                    {
                                                        for (DataSnapshot ps : ds.getChildren())
                                                        {
                                                            mDatabase.child("project_owner").child(postSnapshot.getKey()).child(ps.getKey()).removeValue();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            });
                            adb.show();
                        }
                        if (v == projectViewHolder.edit){
                            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            startActivity(new Intent(getActivity(), EditProject.class));
                            //edNameProject = mDatabase.child("project").child(userId).child(postRef.getKey()).removeValue();
                            FirebaseDatabase database= FirebaseDatabase.getInstance();
                            myRef = database.getReference().child("project").child(userId).child(postRef.getKey());
                            myRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Project project = dataSnapshot.getValue(Project.class);

                                            idproject = postRef.getKey();
                                            edNameProject = project.projectname;
                                            edDescProject = project.projectdesc;
                                            edStart = project.projectstart;
                                            edFinish = project.projectfinish;

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Project p = mutableData.getValue(Project.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "ProjectTransaction:onComplete:" + databaseError);
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
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}