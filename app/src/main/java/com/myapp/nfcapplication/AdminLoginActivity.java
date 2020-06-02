package com.myapp.nfcapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    EditText editUsername,editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        editUsername=findViewById(R.id.editUsername);
        editPassword=findViewById(R.id.editPassword);


        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(editUsername.getText().toString().equalsIgnoreCase("admin")) {
                   editUsername.setText("");
                   editPassword.setText("");
                   startActivity(new Intent(AdminLoginActivity.this, WriteTagActivity.class));
               }else{
                    Toast.makeText(AdminLoginActivity.this, "Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
