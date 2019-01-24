package com.powerproject.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.powerproject.login.R;


public class ProjectViewHolder extends RecyclerView.ViewHolder {

    public TextView projectname;
    public TextView status;
    public Button edit, delete;

    public ProjectViewHolder(View itemView) {
        super(itemView);

        projectname = (TextView) itemView.findViewById(R.id.tvNameProject);
        status = (TextView) itemView.findViewById(R.id.tvStatus);
        edit = (Button) itemView.findViewById(R.id.bEdit);
        delete = (Button) itemView.findViewById(R.id.bDelete);
    }

    public void bindToPost(Project project, View.OnClickListener onClickListener) {
        projectname.setText("Project "+project.projectname);
        status.setText(project.status);
        status.setTextColor(Color.GREEN);

        edit.setOnClickListener(onClickListener);
        delete.setOnClickListener(onClickListener);
    }
}
