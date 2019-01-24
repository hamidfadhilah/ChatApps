package com.powerproject.main;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.powerproject.chart.FitChart;
import com.powerproject.chart.FitChartValue;
import com.powerproject.login.R;
import com.powerproject.progressbar.CustomProgress;

import java.util.ArrayList;
import java.util.Collection;

import static com.powerproject.main.ProjectOwnerFragment.RefProjectOwner;
import static com.powerproject.main.ProjectOwnerFragment.edFinish;
import static com.powerproject.main.ProjectOwnerFragment.edNameProject;
import static com.powerproject.main.ProjectOwnerFragment.edStart;
import static com.powerproject.main.ProjectOwnerFragment.edStatusProject;
import static com.powerproject.main.ProjectOwnerFragment.idproject;

public class OwnerActivity extends AppCompatActivity implements View.OnClickListener {

    CustomProgress customProgressShowProgress;
    TextView tvName, tvStatus, tvStart, tvFinish, tvChangeStatus;
    Dialog dia;

    Spinner sp;
    Button bOke, bCancel;
    String[] items = new String[] {"GREEN","RED"};
    String status;

    private DatabaseReference mDatabase;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        tvName = (TextView) findViewById(R.id.tvOwnerProjectName);
        tvStatus = (TextView) findViewById(R.id.tvOwnerProjectStatus);
        tvStart = (TextView) findViewById(R.id.tvStart);
        tvFinish = (TextView) findViewById(R.id.tvFinish);
        tvChangeStatus = (TextView) findViewById(R.id.tvChangeStatus);
        tvChangeStatus.setOnClickListener(this);

        tvName.setText(edNameProject);
        tvStatus.setText(edStatusProject);
        tvStatus.setTextColor(Color.GREEN);
        tvStart.setText(edStart);
        tvFinish.setText(edFinish);
        status = edStatusProject;

        Resources res = getResources();
        customProgressShowProgress = (CustomProgress) findViewById(R.id.customProgressShowProgress);
        customProgressShowProgress.setMaximumPercentage(1.0f);
        customProgressShowProgress.useRoundedRectangleShape(30.0f);
        customProgressShowProgress.setProgressColor(res.getColor(R.color.green_500));
        customProgressShowProgress.setProgressBackgroundColor(res.getColor(R.color.green_200));
        customProgressShowProgress.setShowingPercentage(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        /*
        final FitChart fitChart = (FitChart)findViewById(R.id.fitChart);
        fitChart.setMinValue(0f);
        fitChart.setMaxValue(100f);

                Resources resources = getResources();
                Collection<FitChartValue> values = new ArrayList<>();
                values.add(new FitChartValue(30f, resources.getColor(R.color.chart_value_1)));
                values.add(new FitChartValue(20f, resources.getColor(R.color.chart_value_2)));
                values.add(new FitChartValue(15f, resources.getColor(R.color.chart_value_3)));
                //values.add(new FitChartValue(10f, resources.getColor(R.color.chart_value_4)));
                fitChart.setValues(values);
                */
    }

    @Override
    public void onClick(View v) {
        if(v == tvChangeStatus){
            dia = new Dialog(this);
            dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dia.setContentView(R.layout.activity_change_status_project);
            dia.setCancelable(false);
            dia.show();
            bOke = (Button) dia.findViewById(R.id.bOke);
            bCancel = (Button) dia.findViewById(R.id.bCloase);
            sp = (Spinner) dia.findViewById(R.id.spStatus);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, items);

            sp.setAdapter(adapter);

            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.v("item", (String) parent.getItemAtPosition(position));
                    status = sp.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });

            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dia.dismiss();
                }
            });

            bOke.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    RefProjectOwner.child("status").setValue(status);

                    mDatabase.child("project").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot)
                        {
                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                mDatabase.child("project").child(postSnapshot.getKey()).child(idproject).child("status").setValue(status);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    dia.dismiss();
                    tvStatus.setText(status);
                    if(status.equals("GREEN")) {
                        tvStatus.setTextColor(Color.GREEN);
                    } else if(status.equals("RED")) {
                        tvStatus.setTextColor(Color.RED);
                    }
                }
            });

        }
    }
}
