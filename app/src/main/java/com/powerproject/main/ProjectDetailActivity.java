package com.powerproject.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.powerproject.login.R;
import com.powerproject.login.User;
import com.powerproject.stateprogress.StateProgressBar;

import java.util.Map;

import static com.powerproject.main.CreateTask.refTaskProject;
import static com.powerproject.main.ProjectFragment.edDescProject;
import static com.powerproject.main.ProjectFragment.edFinish;
import static com.powerproject.main.ProjectFragment.edNameProject;
import static com.powerproject.main.ProjectFragment.edStart;
import static com.powerproject.main.ProjectFragment.edStatusProject;
import static com.powerproject.main.ProjectFragment.idproject;
import static com.powerproject.main.ProjectFragment.myRef;
import static com.powerproject.main.ProjectFragment.postRef1;

public class ProjectDetailActivity extends Activity {
    private static final String TAG = "ProjectDetailActivity";
    private static final String REQUIRED = "Required";
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    private DatabaseReference myRefTask;
    private DatabaseReference postRefTask;
    public static String tp_membername, tp_projectname, tp_taskcategory, tp_taskdesc, tp_taskstart, tp_taskfinish, tp_status, tp_idtask;


    private FirebaseRecyclerAdapter<TaskProject, TaskViewHolder> mAdapter;
    private FirebaseRecyclerAdapter<User, MemberViewHolder> AdapterOwner;
    private RecyclerView mRecycler, rvOwner;
    private LinearLayoutManager mManager, mManager1;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // [END declare_database_ref]
    protected String[] descriptionStatus1 = {"Not Started", "Progress", "Complete"};
    protected StateProgressBar stateprogressbar;
    Button bNext, bClose;
    TextView projectName, projectStatus, projectDate, member;
    Button bAddOwner;
    EditText etOwner;
    Dialog dia;
    String status, idtask, taskstatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        projectName = (TextView) findViewById(R.id.tvNameProject);
        member = (TextView) findViewById(R.id.tvMember);
        projectStatus = (TextView) findViewById(R.id.tvStatus);
        projectDate = (TextView) findViewById(R.id.tvDate);
        etOwner = (EditText) findViewById(R.id.etAddPO);

        bAddOwner = (Button) findViewById(R.id.bAddPO);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // [START create_database_reference]
        // [END create_database_reference]

        mRecycler = (RecyclerView) findViewById(R.id.rvTask);
        mRecycler.setHasFixedSize(true);
        rvOwner = (RecyclerView) findViewById(R.id.rvOwner);
        rvOwner.setHasFixedSize(true);

        projectName.setText("Project "+edNameProject);
        projectStatus.setText("Status : "+edStatusProject);
        projectDate.setText("Date   : "+edStart+" - "+edFinish);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up Layout Manager, reverse layout
        mManager1 = new LinearLayoutManager(this);
        mManager1.setReverseLayout(true);
        mManager1.setStackFromEnd(true);
        rvOwner.setLayoutManager(mManager1);

        //set up Owner
        Query postsOwner = myRef.child("project_owner");
        AdapterOwner = new FirebaseRecyclerAdapter<User, MemberViewHolder>(User.class, R.layout.layout_member,
                MemberViewHolder.class,  postsOwner) {
            @Override
            protected void populateViewHolder(final MemberViewHolder memberViewHolder, User user, int i) {
                final DatabaseReference postRefmember = getRef(i);

                // Set click listener for the whole post view
                final String postKey = postRefmember.getKey();
                if(AdapterOwner.getItemCount() != 0){
                    bAddOwner.setEnabled(false);
                    etOwner.setEnabled(false);
                } else if(AdapterOwner.getItemCount() == 0){
                    bAddOwner.setEnabled(true);
                    etOwner.setEnabled(true);
                }
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                memberViewHolder.bindToPost(user, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Need to write to both places the post is stored

                        if (v == memberViewHolder.delete) {
                            AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                            adb.setTitle("Delete?");
                            adb.setMessage("Are you sure you want to delete this member?");
                            adb.setNegativeButton("Cancel", null);
                            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bAddOwner.setEnabled(true);
                                    etOwner.setEnabled(true);
                                    myRef.child("project_owner").child(postKey).removeValue();

                                    mDatabase.child("project_owner").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot)
                                        {
                                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                            {
                                                //mDatabase.child("project_owner").child(postSnapshot.getKey()).removeValue();
                                                Log.d("User : ", postSnapshot.getKey());
                                                Log.d("User : ", postRef1.getKey());
                                                mDatabase.child("project_owner").child(postSnapshot.getKey()).orderByChild("id_project").equalTo(postRef1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    }

                });

            }
        };
        rvOwner.setAdapter(AdapterOwner);


        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<TaskProject, TaskViewHolder> (TaskProject.class, R.layout.layout_task,
                TaskViewHolder.class,  postsQuery) {
            @Override
            protected void populateViewHolder(final TaskViewHolder taskViewHolder, final TaskProject taskProject, int i) {
                postRefTask = getRef(i);

                // Set click listener for the whole post view
                final String postKey = postRefTask.getKey();
                taskViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database= FirebaseDatabase.getInstance();
                        myRefTask = database.getReference().child("project").child(userId).child(idproject).child("task_project").child(postKey);
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
                taskViewHolder.bindToPost(taskProject, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == taskViewHolder.delete){
                            AlertDialog.Builder adb=new AlertDialog.Builder(v.getContext());
                            adb.setTitle("Delete?");
                            adb.setMessage("Are you sure you want to delete this task?");
                            adb.setNegativeButton("Cancel", null);
                            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myRef.child("task_project").child(postKey).removeValue();

                                    mDatabase.child("project_member").orderByChild("id_task").equalTo(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                            {
                                                mDatabase.child("project_member").child(postSnapshot.getKey()).removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    mDatabase.child("task_member").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot)
                                        {
                                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                            {
                                                //mDatabase.child("project_owner").child(postSnapshot.getKey()).removeValue();
                                                Log.d("User : ", postSnapshot.getKey());
                                                Log.d("User : ", postKey);
                                                mDatabase.child("task_member").child(postSnapshot.getKey()).orderByChild("id_task").equalTo(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                }
                            });
                            adb.show();
                        }
                        if (v == taskViewHolder.edit){
                            myRef.child("task_project").child(postKey).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            TaskProject project = dataSnapshot.getValue(TaskProject.class);

                                            tp_idtask = postKey;
                                            tp_membername = project.membername;
                                            tp_projectname = project.projectname;
                                            tp_status = project.status;
                                            tp_taskcategory = project.taskcategory;
                                            tp_taskstart = project.taskstart;
                                            tp_taskfinish = project.taskfinish;

                                            tp_taskdesc = project.taskdesc;
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            startActivity(new Intent(ProjectDetailActivity.this, EditTask.class));
                        }
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);

        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProjectDetailActivity.this, AddMemberActivity.class));
            }
        });
        // Add Project Owner
        bAddOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String owner = etOwner.getText().toString();
                etOwner.setText("");

                /*
                mDatabase.child("project_owner").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {
                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            mDatabase.child("project_owner").child(postSnapshot.getKey()).orderByChild("id_project").equalTo(idproject).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot ds)
                                {
                                    for (DataSnapshot child : ds.getChildren()) {
                                        Map<String, Object> model = (Map<String, Object>) child.getValue();
                                        if(model.get("id_project").equals(idproject)) {
                                            mDatabase.child("project_owner").child(postSnapshot.getKey()).child(child.getKey()).removeValue();
                                            break;
                                        }
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
                */
                mDatabase.child("users").orderByChild("email").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            User projectowner = new User((String) postSnapshot.child("email").getValue(), (String) postSnapshot.child("username").getValue());
                            myRef.child("project_owner").child(postSnapshot.getKey()).setValue(projectowner);
                            String keyProjectOwner = mDatabase.child("project_owner").push().getKey();
                            ProjectOwner projectOwner = new ProjectOwner(idproject, edNameProject, edDescProject, edStart, edFinish, edStatusProject);
                            mDatabase.child("project_owner").child(userId + "/" + keyProjectOwner).setValue(projectOwner);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ProjectDetailActivity.this, "Member Not Found",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void popupagree() {
        dia = new Dialog(this);
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.setContentView(R.layout.activity_edittask);
        dia.setCancelable(false);
        dia.show();

        bNext = (Button) dia.findViewById(R.id.bNext);
        bClose = (Button) dia.findViewById(R.id.bClose);

        stateprogressbar = (StateProgressBar) dia.findViewById(R.id.usage_stateprogressbar);
        stateprogressbar.setStateDescriptionData(descriptionStatus1);

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
                        TaskProject taskMember = dataSnapshot.getValue(TaskProject.class);
                        idtask =  taskMember.id_task;
                        dataSnapshot.getRef().child("status").setValue(taskstatus);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });

                mDatabase.child("task_member").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {
                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            mDatabase.child("task_member").child(postSnapshot.getKey()).orderByChild("id_task").equalTo(idtask).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot ds)
                                {
                                    for (DataSnapshot ps : ds.getChildren())
                                    {
                                        mDatabase.child("task_member").child(postSnapshot.getKey()).child(ps.getKey()).child("status").setValue(taskstatus);
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

    private Query getQuery(DatabaseReference mDatabase) {
        return myRef.child("task_project");
    }
}
