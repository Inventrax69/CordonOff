package com.myapp.nfcapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndConditionActivity extends AppCompatActivity {

    TextView btnAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);


        btnAgree= (TextView) findViewById(R.id.btnAgree);

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(login);
                finish();
            }
        });




    }
}
