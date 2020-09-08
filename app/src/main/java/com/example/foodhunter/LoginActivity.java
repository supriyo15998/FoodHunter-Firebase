package com.example.foodhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.sessions.LocalSessionStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView tvRegister;
    private TextInputEditText etEmail, etPass;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private String email,pass;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString();
                pass = etPass.getText().toString();
                if(email.isEmpty()) etEmail.setError("Please provide email id");
                else if(pass.isEmpty()) etPass.setError("Please provide password");
                else {
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseUser fUser = firebaseAuth.getCurrentUser();
                                boolean check = fUser.isEmailVerified();
                                if(check) {
                                    LocalSessionStore localSessionStore = new LocalSessionStore(LoginActivity.this);
                                    localSessionStore.storeData(StaticData.USER_ID,fUser.getUid());
                                    localSessionStore.storeData(StaticData.USER_EMAIL,fUser.getEmail());
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginActivity.this,WelcomeBottomActivity.class));
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Please verify your email first", Toast.LENGTH_LONG).show();
                                }
                                LoginActivity.this.finish();
                            }
                            else {
                                progressDialog.dismiss();
                                Log.d("LoginActivity","Something went wrong");
                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}