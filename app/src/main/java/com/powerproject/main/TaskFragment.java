package com.powerproject.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.powerproject.login.R;
import com.powerproject.login.RegisterActivity;
import com.powerproject.login.User;
import com.powerproject.stateprogress.StateProgressBar;

import static com.powerproject.main.ProjectFragment.myRef;

public class TaskFragment extends Fragment {

    private FirebaseRecyclerAdapter<TaskMember, MyTaskViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private DatabaseReference mDatabase;
    public static DatabaseReference myRefTask;
    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Dialog dia;

    protected String[] descriptionStatus = {"Not Started", "Progress", "Complete"};
    protected StateProgressBar stateprogressbar;
    Button bNext, bClose;
    String status,idtask, taskstatus;

    public TaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRefTask = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.rvMyTask);
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
        Query postsQuery = getQuery(myRefTask);
        mAdapter = new FirebaseRecyclerAdapter<TaskMember, MyTaskViewHolder>(TaskMember.class, R.layout.layout_mytask,
                MyTaskViewHolder.class,  postsQuery) {
            @Override
            protected void populateViewHolder(final MyTaskViewHolder taskViewHolder, TaskMember task, int i) {
                final DatabaseReference postRefmember = getRef(i);

                // Set click listener for the whole post view
                final String postKey = postRefmember.getKey();
                taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database= FirebaseDatabase.getInstance();
                        myRefTask = database.getReference().child("task_member").child(userId).child(postKey);
                        myRefTask.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        TaskMember project = dataSnapshot.getValue(TaskMember.class);

                                        taskstatus = project.status;
                                        Log.d("Status : ", taskstatus);
                                        popupagree();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                });
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                taskViewHolder.bindToPost(task, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Need to write to both places the post is stored
                        }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);


    }

    private void popupagree() {
        dia = new Dialog(getActivity());
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.setContentView(R.layout.activity_edittask);
        dia.setCancelable(false);
        dia.show();
        bNext = (Button) dia.findViewById(R.id.bNext);
        bClose = (Button) dia.findViewById(R.id.bClose);

        stateprogressbar = (StateProgressBar) dia.findViewById(R.id.usage_stateprogressbar);
        stateprogressbar.setStateDescriptionData(descriptionStatus);

        if(taskstatus.equals("Not Started")){
            stateprogressbar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
        } else if(taskstatus.equals("Progress")){
            stateprogressbar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        } else if(taskstatus.equals("Completed")){
            stateprogressbar.setAllStatesCompleted(true);
            stateprogressbar.enableAnimationToCurrentState(false);
            bNext.setEnabled(false);
        }

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });

        bNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(taskstatus.equals("Not Started")){
                    stateprogressbar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                    taskstatus = "Progress";
                } else if(taskstatus.equals("Progress")){
                    stateprogressbar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                    taskstatus = "Completed";
                    bNext.setEnabled(false);
                }

                myRefTask.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TaskMember taskMember = dataSnapshot.getValue(TaskMember.class);
                        idtask =  taskMember.id_task;
                        dataSnapshot.getRef().child("status").setValue(taskstatus);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });

                mDatabase.child("project").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {
                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            mDatabase.child("project").child(postSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot ds)
                                {
                                    for (final DataSnapshot ps : ds.getChildren())
                                    {
                                        mDatabase.child("project").child(postSnapshot.getKey()).child(ps.getKey()).child("task_project").orderByChild("id_task").equalTo(idtask).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot ds)
                                            {
                                                for (DataSnapshot ps1 : ds.getChildren())
                                                {
                                                    mDatabase.child("project").child(postSnapshot.getKey()).child(ps.getKey()).child("task_project").child(ps1.getKey()).child("status").setValue(taskstatus);
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
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    private Query getQuery(DatabaseReference myRefTask) {
        return myRefTask.child("task_member")
                .child(userId);
    }
}
