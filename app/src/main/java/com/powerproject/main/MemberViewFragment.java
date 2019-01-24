package com.powerproject.main;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by COEG-IN on 4/20/2017.
 */

public class MemberViewFragment extends ProjectMemberFragment {

    public MemberViewFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("project_member")
                .child(getUid());
    }
}
