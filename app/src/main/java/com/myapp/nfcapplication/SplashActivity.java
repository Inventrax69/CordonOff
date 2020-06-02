package com.myapp.nfcapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences prefs;
    int is_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                prefs = getSharedPreferences("CordonOff", MODE_PRIVATE);
                is_login = prefs.getInt(KeyValues.IS_LOGIN, 0);
                if(is_login==1){
                    startActivity(new Intent(SplashActivity.this, UserActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
                    finish();
                }

            }
        }.start();

    }
}
