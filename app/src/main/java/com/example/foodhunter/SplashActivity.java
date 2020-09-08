package com.example.foodhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.sessions.LocalSessionStore;

public class SplashActivity extends AppCompatActivity {
    LocalSessionStore localSessionStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        localSessionStore = new LocalSessionStore(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(localSessionStore.getData(StaticData.USER_ID).isEmpty())
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this,WelcomeBottomActivity.class));
                SplashActivity.this.finish();
            }
        },1000);
    }
}