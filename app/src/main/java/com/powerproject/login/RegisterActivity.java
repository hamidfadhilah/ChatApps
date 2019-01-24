package com.powerproject.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final int SIGN_IN_REQUEST_CODE = 111;
    EditText etEmail, etPassword, etUsername;
    Button bRegister;
    CheckBox cbCek;
    Dialog dia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);
        cbCek = (CheckBox) findViewById(R.id.bCek);

        bRegister.setOnClickListener(this);
        cbCek.setOnClickListener(this);
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering");
        progressDialog.show();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String username = etUsername.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signUp:onComplete:" + task.isSuccessful());
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Sign Up Success",
                                    Toast.LENGTH_SHORT).show();
                            final FirebaseUser firebaseUser = task.getResult().getUser();
                            Task<Void> updateTask = firebaseUser.updateProfile(
                                    new UserProfileChangeRequest
                                            .Builder()
                                            .setDisplayName(username).build());
                            updateTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }
                                });
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(username, email);
                            mDatabase.child("users").child(userId).setValue(user);
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                            //etPassword.setError("Required max 6");
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            etUsername.setError("Required");
            result = false;
        } else {
            etUsername.setError(null);
        }

        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Required");
            result = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Required");
            result = false;
        } else {
            etPassword.setError(null);
        }

        if (etPassword.length() > 6){
            etPassword.setError("Required max 6");
            result = false;
        }else {
            etPassword.setError(null);
        }
        return result;
    }

    private void popupagree() {
        dia = new Dialog(RegisterActivity.this);
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.setContentView(R.layout.dialog);
        dia.setCancelable(false);
        dia.show();
        TextView tvAgree = (TextView) dia.findViewById(R.id.tvAgree);
        tvAgree.setText("Persyaratan");
        Button btnOke = (Button) dia.findViewById(R.id.btnOke);

        btnOke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dia.dismiss();

            }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = etUsername.getText().toString();

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        if (v == bRegister) {
            signUp();
        }
        if (v == cbCek){
            popupagree();
            if (cbCek.isChecked()) { //ceklis
                bRegister.setEnabled(true);
            }
        }
    }
}
