package com.powerproject.main;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TaskViewFragment extends ProjectFragment {

    public TaskViewFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("project")
                .child(getUid());
    }
}
