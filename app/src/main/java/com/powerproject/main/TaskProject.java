package com.powerproject.main;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by COEG-IN on 4/24/2017.
 */

public class TaskProject {
    public String id_task;
    public String membername;
    public String projectname;
    public String taskcategory;
    public String taskdesc;
    public String taskstart;
    public String taskfinish;
    public String status;

    public TaskProject() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public TaskProject(String id_task , String membername,String projectname, String taskcategory, String taskdesc, String taskStart, String taskFinish, String status) {
        this.id_task = id_task;
        this.membername = membername;
        this.projectname = projectname;
        this.taskcategory = taskcategory;
        this.taskdesc = taskdesc;
        this.taskstart = taskStart;
        this.taskfinish = taskFinish;
        this.status = status;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id_task", id_task);
        result.put("membername", membername);
        result.put("projectname", projectname);
        result.put("taskcategory", taskcategory);
        result.put("taskdesc", taskdesc);
        result.put("taskstart", taskstart);
        result.put("taskfinish", taskfinish);
        result.put("status", status);
        return result;
    }
    // [END post_to_map]
}
