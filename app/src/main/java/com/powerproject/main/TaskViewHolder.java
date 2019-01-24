package com.powerproject.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.powerproject.login.R;


public class TaskViewHolder extends RecyclerView.ViewHolder {

    public TextView taskname;
    public TextView statustask;
    public Button edit, delete;

    public TaskViewHolder(View itemView) {
        super(itemView);

        taskname = (TextView) itemView.findViewById(R.id.tvNameTask);
        statustask = (TextView) itemView.findViewById(R.id.tvStatusTask);
        edit = (Button) itemView.findViewById(R.id.bEdit);
        delete = (Button) itemView.findViewById(R.id.bDelete);
    }

    public void bindToPost(TaskProject taskProject, View.OnClickListener onClickListener) {
        taskname.setText("Task   : "+taskProject.taskcategory);
        statustask.setText("Status : "+taskProject.status);

        edit.setOnClickListener(onClickListener);
        delete.setOnClickListener(onClickListener);
    }
}
