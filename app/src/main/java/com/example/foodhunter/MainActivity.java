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
import com.example.foodhunter.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText etName, etEmail, etPhone, etPass, etConfPass,etAddress;
    ProgressDialog progressDialog;
    private Button btnSignUp;
    private TextView tvLogin;
    private String name,email,pass,phone,confirm,address;
    private boolean isAdmin = false;
    private FirebaseAuth mAuth;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etContact);
        etPass = findViewById(R.id.etPassword);
        etConfPass = findViewById(R.id.etConfPassword);
        btnSignUp = findViewById(R.id.btnSignUn);
        tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameError = false, isPhoneError = false, isMailError = false, isPassError = false, isAddressError = false;
                name = etName.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                phone = etPhone.getText().toString().trim();
                address = etAddress.getText().toString().trim();
                pass = etPass.getText().toString().trim();
                confirm = etConfPass.getText().toString().trim();
                if(name.isEmpty()) {
                    etName.setError("Please specify your name");
                    isNameError = true;
                }
                if(email.isEmpty()) {
                    etEmail.setError("Please provide your email");
                    isMailError = true;
                }
                if(address.isEmpty()) {
                    etAddress.setError("Please provide your address");
                    isAddressError = true;
                }
                if(phone.length() != 10) {
                    etPhone.setError("Please provide your contact number correctly");
                    isPhoneError = true;
                }
                if(pass.isEmpty()) {
                    etPass.setError("Password cannot be empty");
                    isPassError = true;
                }
                if(!confirm.equals(pass)) {
                    etPass.setError("Two passwords didn't match");
                    etConfPass.setError("Two passwords didn't match");
                    isPassError = true;
                }


                if(!isPassError && !isMailError && !isPhoneError && !isNameError && !isAddressError) {
                    Log.d("MainActivity","All good to go!");
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String userId = mAuth.getUid();
                                user = new User(userId,name,email,phone,pass,isAdmin,address);
                                createProfile(user,"Sign Up Successful");
                                Log.d("MainActivity","User " + userId + " Created");
                                //Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                Log.d("MainActivity","Task Failed Successfully!");
                                Toast.makeText(MainActivity.this, "Task failed successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void createProfile(User user, String sign_up_successful) {
        mAuth.getCurrentUser().sendEmailVerification();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> newUser = new HashMap<>();
        newUser.put(StaticData.USER_ID,user.getUserId());
        newUser.put(StaticData.USER_NAME,user.getUserName());
        newUser.put(StaticData.USER_EMAIL,user.getUserEmail());
        newUser.put(StaticData.USER_CONTACT,user.getUserContact());
        newUser.put(StaticData.USER_PASSWORD,user.getUserPass());
        newUser.put(StaticData.USER_DP,"");
        newUser.put(StaticData.IS_ADMIN,user.getIsAdmin());
        newUser.put(StaticData.USER_ADDRESS,user.getUserAddress());
        db.collection("users").document(user.getUserId()).set(newUser).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                MainActivity.this.finish();
            }
        });
    }
}