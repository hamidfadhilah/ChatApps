package com.powerproject.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.powerproject.login.R;
import com.powerproject.login.User;


public class MemberViewHolder extends RecyclerView.ViewHolder {

    public TextView username;
    public TextView emailuser;
    public Button delete;

    public MemberViewHolder(View itemView) {
        super(itemView);

        username = (TextView) itemView.findViewById(R.id.tvNameMember);
        emailuser = (TextView) itemView.findViewById(R.id.tvEmailMember);
        delete = (Button) itemView.findViewById(R.id.bDelete);
    }

    public void bindToPost(User user, View.OnClickListener onClickListener) {
        username.setText(user.username);
        emailuser.setText(user.email);

        delete.setOnClickListener(onClickListener);
    }
}
