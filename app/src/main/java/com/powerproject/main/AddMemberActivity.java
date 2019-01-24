package com.powerproject.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import static com.powerproject.main.ProjectFragment.edDescProject;
import static com.powerproject.main.ProjectFragment.edFinish;
import static com.powerproject.main.ProjectFragment.edNameProject;
import static com.powerproject.main.ProjectFragment.edStart;
import static com.powerproject.main.ProjectFragment.edStatusProject;
import static com.powerproject.main.ProjectFragment.idproject;
import static com.powerproject.main.ProjectFragment.myRef;

public class AddMemberActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseRecyclerAdapter<User, MemberViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference myRefMember;
    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static String memberId, memberName, memberEmail;

    EditText etAddmember;
    Button bAdd, bFinish;
    String memberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        etAddmember = (EditText) findViewById(R.id.etAddMember);
        bAdd = (Button) findViewById(R.id.bAddMember);
        bFinish = (Button) findViewById(R.id.bBackMember);

        bFinish.setOnClickListener(this);
        bAdd.setOnClickListener(this);

        mRecycler = (RecyclerView) findViewById(R.id.rvOwner);
        mRecycler.setHasFixedSize(true);
        myRefMember = FirebaseDatabase.getInstance().getReference();

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(myRefMember);
        mAdapter = new FirebaseRecyclerAdapter<User, MemberViewHolder>(User.class, R.layout.layout_member,
                MemberViewHolder.class,  postsQuery) {
            @Override
            protected void populateViewHolder(final MemberViewHolder memberViewHolder, User user, int i) {
                final DatabaseReference postRefmember = getRef(i);

                // Set click listener for the whole post view
                final String postKey = postRefmember.getKey();

                memberViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AddMemberActivity.this, CreateTask.class));
                        myRef.child("project_member").child(postKey).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User member = dataSnapshot.getValue(User.class);

                                        memberId = postKey;
                                        memberEmail = member.email;
                                        memberName = member.username;
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });
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
                                    myRef.child("project_member").child(postKey).addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    User member = dataSnapshot.getValue(User.class);

                                                    memberId = postKey;
                                                    memberEmail = member.email;
                                                    memberName = member.username;
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                    myRef.child("project_member").child(postKey).removeValue();

                                    myRef.child("task_project").orderByChild("membername").equalTo(memberName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot ds)
                                                    {
                                                        Log.d("Member : ", memberName);
                                                        for (DataSnapshot ps : ds.getChildren())
                                                        {
                                                            myRef.child("task_project").child(ps.getKey()).removeValue();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });



                                    mDatabase.child("project_member").child(postKey).orderByChild("id_project").equalTo(idproject).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot ds)
                                        {
                                            for (DataSnapshot ps : ds.getChildren())
                                            {
                                                mDatabase.child("project_member").child(postKey).child(ps.getKey()).removeValue();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    mDatabase.child("task_member").child(postKey).orderByChild("id_project").equalTo(idproject).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot ds)
                                        {
                                            for (DataSnapshot ps : ds.getChildren())
                                            {
                                                mDatabase.child("task_member").child(postKey).child(ps.getKey()).removeValue();
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
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        if ( v == bAdd){
            String member = etAddmember.getText().toString();
            etAddmember.setText("");

            mDatabase.child("users").orderByChild("email").equalTo(member).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        memberId = postSnapshot.getKey();

                        User projectmember = new User((String)postSnapshot.child("username").getValue(), (String)postSnapshot.child("email").getValue() );
                        myRef.child("project_member").child(postSnapshot.getKey()).setValue(projectmember);
                        mRecycler.invalidate();

                        String keyProjectMember = mDatabase.child("project_member").push().getKey();
                        ProjectMember projectMember = new ProjectMember(idproject, edNameProject, edDescProject, edStart, edFinish,edStatusProject);
                        mDatabase.child("project_member").child(memberId+"/"+keyProjectMember).setValue(projectMember);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(AddMemberActivity.this, "Member Not Found",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (v == bFinish){
            finish();
        }
    }

    private Query getQuery(DatabaseReference child) {
        return myRef.child("project_member");
    }
}
