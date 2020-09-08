package com.example.foodhunter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.sessions.LocalSessionStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class WelcomeBottomActivity extends AppCompatActivity {
    LocalSessionStore localSessionStore;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_bottom);
        ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
        },900);
        //set user address to session
        localSessionStore = new LocalSessionStore(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(localSessionStore.getData(StaticData.USER_ID)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    //Log.d("WelcomeBottomActivity",task.getResult().get("address").toString());
                    localSessionStore.storeData(StaticData.USER_ADDRESS,task.getResult().get("address").toString());
                    //Log.d("address in session",localSessionStore.getData(StaticData.USER_ADDRESS));
                }else {
                    Toast.makeText(WelcomeBottomActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(WelcomeBottomActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                performLogout();
                break;
            case R.id.menu_cart:
                startActivity(new Intent(this,CartActivitiy.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        localSessionStore = new LocalSessionStore(WelcomeBottomActivity.this);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(WelcomeBottomActivity.this);
        alertBuilder.setMessage("Do you really want to exit?");
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                localSessionStore.clearData();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(WelcomeBottomActivity.this,LoginActivity.class));
                WelcomeBottomActivity.this.finish();
            }
        });
        alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertBuilder.show();
    }
}