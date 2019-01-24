package com.powerproject.main;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class ProjectViewFragment extends ProjectFragment {

    public ProjectViewFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my project
        return databaseReference.child("project")
                .child(getUid());
    }
}
