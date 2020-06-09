package com.myapp.nfcapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndConditionActivity extends AppCompatActivity {

    TextView btnAgree;
    SharedPreferences prefs;
    private int isTermsAndConditionsAgreed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        prefs = getSharedPreferences("CordonOff", MODE_PRIVATE);
        isTermsAndConditionsAgreed = prefs.getInt(KeyValues.IS_HOME_QUARANTINE, 0);

        btnAgree= (TextView) findViewById(R.id.btnAgree);

        if(isTermsAndConditionsAgreed != 0){
            btnAgree.setVisibility(View.GONE);
        }else {
            btnAgree.setVisibility(View.VISIBLE);
        }

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                editor.putInt(KeyValues.TERMS_AND_CONDITIONS_AGREED, 1);
                editor.apply();
                Intent login = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(login);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isTermsAndConditionsAgreed != 0){
            Intent login = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(login);
            finish();
        }
    }
}
