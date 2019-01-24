package com.powerproject.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.powerproject.login.R;

/**
 * Created by COEG-IN on 5/1/2017.
 */

public class ProjectMemberViewHolder extends RecyclerView.ViewHolder {
    public TextView projectname;
    public TextView status;


    public ProjectMemberViewHolder(View itemView) {
        super(itemView);

        projectname = (TextView) itemView.findViewById(R.id.tvNameProject);
        status = (TextView) itemView.findViewById(R.id.tvStatus);

    }

    public void bindToPost(ProjectMember project, View.OnClickListener onClickListener) {
        projectname.setText("Project "+project.projectname);
        status.setText(project.status);
        status.setTextColor(Color.GREEN);
    }
}
