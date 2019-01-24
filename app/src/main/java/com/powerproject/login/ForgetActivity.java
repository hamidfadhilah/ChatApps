package com.powerproject.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgetActivity extends Activity implements View.OnClickListener{
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    EditText etEmail;
    Button btFContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        etEmail = (EditText) findViewById(R.id.etEmail);
        btFContinue = (Button) findViewById(R.id.btFContinue);

        btFContinue.setOnClickListener(this);
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Required");
            result = false;
        } else {
            etEmail.setError(null);
        }
        return result;
    }

        @Override
    public void onClick(View v) {
        if(v == btFContinue){
            if (!validateForm()) {
                return;
            }
            String email = etEmail.getText().toString();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sending");
            progressDialog.show();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetActivity.this, "Check your email",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetActivity.this, LoginActivity.class));
                            }
                        }
                    });
        }
    }
}
