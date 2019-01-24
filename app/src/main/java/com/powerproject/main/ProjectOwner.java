package com.powerproject.main;

/**
 * Created by COEG-IN on 5/1/2017.
 */

public class ProjectOwner {
    public String id_project;
    public String projectname;
    public String projectdesc;
    public String projectstart;
    public String projectfinish;
    public String status;
    //public int starCount = 0;
    //public Map<String, Boolean> stars = new HashMap<>();

    public ProjectOwner() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public ProjectOwner(String uid, String projectname, String projectdesc, String projectstart, String projectfinish, String status) {
        this.id_project = uid;
        this.projectname = projectname;
        this.projectdesc = projectdesc;
        this.projectstart = projectstart;
        this.projectfinish = projectfinish;
        this.status = status;
    }
}
