package com.myapp.nfcapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

public class OTPActivity extends AppCompatActivity {

    Button btnVerify;
    TextInputEditText etOTP;
    private int customerID;

    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);

        toolBar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnVerify = findViewById(R.id.btnVerify);
        etOTP = findViewById(R.id.etOTP);
        customerID = getIntent().getIntExtra(KeyValues.CUSTOMERID, 0);



        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etOTP.getText().toString().equalsIgnoreCase("1234")) {
                    Intent intent = new Intent(OTPActivity.this, TravelHistoryTo.class);
                    intent.putExtra(KeyValues.CUSTOMERID, customerID);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(OTPActivity.this, "Please enter valid OTP number which is sent to your mobile", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
