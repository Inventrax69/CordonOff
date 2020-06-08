package com.myapp.nfcapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.myapp.nfcapplication.Interface.ApiInterface;
import com.myapp.nfcapplication.Pojo.CustomerRegistration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {

    EditText editUsername,editPassword;

    Dialog dialog;
    CardView cvLoginAsAdmin,cvSelfReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        editUsername=findViewById(R.id.editUsername);
        editPassword=findViewById(R.id.editPassword);
        cvLoginAsAdmin=findViewById(R.id.cvLoginAsAdmin);
        cvSelfReg=findViewById(R.id.cvSelfReg);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Hiding keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                if(!editUsername.getText().toString().isEmpty())
                  getDetails();
                else
                    Toast.makeText(UserLoginActivity.this, "Enter Aadhar/Passport number to proceed", Toast.LENGTH_SHORT).show();

            }
        });

        cvLoginAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLoginActivity.this, AdminLoginActivity.class));
            }
        });

        cvSelfReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLoginActivity.this, RegistrationActivity.class));
            }
        });
    }


    public void getDetails() {

        setProgressDialog();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setAadharNumber(editUsername.getText().toString());

        Call<String> call = null;
        call = apiService.GETCustomerData(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerDto;
                dialog.dismiss();
                if (response.body() != null) {

                    customerDto = new Gson().fromJson(response.body(), CustomerRegistration.class);

                    if(customerDto.getCustomerID()!=0) {

                        if(customerDto.getIsRegistrationCompleted()!=0) {

                            if(!customerDto.getQRCode().isEmpty() || customerDto.getIsMobileNFCEnabled()!=0) {

                                SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                                editor.putString(KeyValues.NAME_OF_THE_PERSON, customerDto.getFirstName());
                                editor.putInt(KeyValues.CUSTOMERID, customerDto.getCustomerID());
                                editor.putString(KeyValues.AADHAR_NUMBER, customerDto.getAadharNumber());
                                editor.putString(KeyValues.MOBILE_ONE, customerDto.getMobileNo1());
                                editor.putString(KeyValues.MOBILE_TWO, customerDto.getMobileNo2());
                                editor.putString(KeyValues.ADDRESS_REGISTERED, customerDto.getAddressLine1());
                                editor.putInt(KeyValues.IS_HOME_QUARANTINE, customerDto.getIsHomeQuarantine());
                                editor.putString(KeyValues.LATITUDE, customerDto.getLatitude());
                                editor.putString(KeyValues.LONGITUDE, customerDto.getLongitude());
                                editor.putInt(KeyValues.IS_MOBILE_SMARTPHONE, customerDto.getIsMobileSmartPhone());
                                editor.putInt(KeyValues.IS_NFC_ENABLED, customerDto.getIsMobileNFCEnabled());
                                editor.putString(KeyValues.STATE, customerDto.getState());
                                editor.putString(KeyValues.COUNTRY, customerDto.getCountry());
                                editor.putInt(KeyValues.TRAVEL_HISTORY_ID, customerDto.getTravelHistoryID());
                                editor.putString(KeyValues.CREATED_DATE, customerDto.getCreatedDate());
                                editor.putString(KeyValues.QRCODE, customerDto.getQRCode());
                                editor.putInt(KeyValues.VIOLATION_COUNT, customerDto.getVialotionCount());
                                editor.putInt(KeyValues.IS_LOGIN, 1);
                                editor.apply();

                                editUsername.setText("");

                                startActivity(new Intent(UserLoginActivity.this, UserActivity.class));
                                finish();

                            }else {
                                Toast.makeText(UserLoginActivity.this, "Tag activation not yet done. Please do active tag.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(UserLoginActivity.this, "Registration not completed", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(UserLoginActivity.this, "Aadhar/Passport number is not registered.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(UserLoginActivity.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(UserLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setProgressDialog() {

        int llPadding = 5;
        LinearLayout ll = new LinearLayout(UserLoginActivity.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(UserLoginActivity.this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(UserLoginActivity.this);
        //tvText.setText("Please wait..");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserLoginActivity.this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }


}
