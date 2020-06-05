package com.myapp.nfcapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.myapp.nfcapplication.Interface.ApiInterface;
import com.myapp.nfcapplication.Pojo.CustomerRegistration;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.M)
public class TravelHistoryFrom extends AppCompatActivity {

    public static final String TAG = TravelHistoryFrom.class.getSimpleName();

    TextView txtData;
    TextInputEditText addressTravelHistory, etPinCodeTravelHistory;
    AutoCompleteTextView countryDropDown, stateDropDown, cityDropDown;
    TextInputLayout txtInptCity;
    Button btnClear, btnSave;

    // FusedLocationProviderClient mFusedLocationClient;
    private ArrayAdapter<String> adapterStates;
    private ArrayAdapter<String> adapterCities;
    private ArrayAdapter<String> adapterCountries;

    String selectedCountry = "", selectedState = "";

    Dialog dialog;

    private int customerID;

    Toolbar toolBar;

    Gson gson;
    Dialog dialogRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveling_from);

        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);

        toolBar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtData = findViewById(R.id.txtData);

        countryDropDown = (AutoCompleteTextView) findViewById(R.id.countryDropDown);
        stateDropDown = (AutoCompleteTextView) findViewById(R.id.stateDropDown);
        cityDropDown = (AutoCompleteTextView) findViewById(R.id.cityDropDown);
        addressTravelHistory = (TextInputEditText) findViewById(R.id.addressTravelHistory);
        etPinCodeTravelHistory = (TextInputEditText) findViewById(R.id.etPinCodeTravelHistory);

        txtInptCity = (TextInputLayout) findViewById(R.id.txtInptCity);


        btnClear = (Button) findViewById(R.id.btnClear);
        btnSave = (Button) findViewById(R.id.btnSave);

        stateDropDown.setEnabled(false);

        gson = new Gson();
        customerID = getIntent().getIntExtra(KeyValues.CUSTOMERID, 0);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hiding keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                if (!countryDropDown.getText().toString().isEmpty() || !stateDropDown.getText().toString().isEmpty() || !addressTravelHistory.getText().toString().isEmpty() || !etPinCodeTravelHistory.getText().toString().isEmpty())
                    submitTravelHistory();
                else
                    Toast.makeText(TravelHistoryFrom.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTravelHistory();
            }
        });

        countryDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!countryDropDown.getAdapter().getItem(position).toString().equalsIgnoreCase("Select")) {
                    selectedCountry = countryDropDown.getAdapter().getItem(position).toString();
                    stateDropDown.setEnabled(true);
                    GetStatesbasedonCountry(selectedCountry);
                } else {
                    Toast.makeText(TravelHistoryFrom.this, "Please select country", Toast.LENGTH_SHORT).show();
                }

            }
        });

        stateDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!stateDropDown.getAdapter().getItem(position).toString().equalsIgnoreCase("Select")) {
                    selectedState = stateDropDown.getAdapter().getItem(position).toString();
                    if (selectedCountry.equalsIgnoreCase("India")) {
                        cityDropDown.setVisibility(View.VISIBLE);
                        txtInptCity.setVisibility(View.VISIBLE);
                    } else {
                        cityDropDown.setVisibility(View.GONE);
                        txtInptCity.setVisibility(View.GONE);
                    }

                    GetCitybasedonState(selectedState);
                } else {
                    Toast.makeText(TravelHistoryFrom.this, "Please select State", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getCountries();

    }


    public void submitTravelHistory() {

        setProgressDialog();
        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setAddressLine1(addressTravelHistory.getText().toString());
        customerRegistrationDto.setPinCode(etPinCodeTravelHistory.getText().toString());
        if (!cityDropDown.getText().toString().isEmpty()) {
            customerRegistrationDto.setCity(cityDropDown.getText().toString());
        }
        customerRegistrationDto.setState(stateDropDown.getText().toString());
        customerRegistrationDto.setCountry(countryDropDown.getText().toString());
        customerRegistrationDto.setCustomerID(customerID);

        Call<String> call = null;
        call = apiService.CustomerTravelHistory(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerRegistrationDto;
                dialog.dismiss();
                if (response.body() != null) {

                    customerRegistrationDto = new Gson().fromJson(response.body(), CustomerRegistration.class);
                    if (customerRegistrationDto.getIsRegistrationCompleted() != 0) {
                        dialog.dismiss();
                        //MaterialDialogUtils.showUploadSuccessDialog(TravelHistoryFrom.this, "Uploaded");
                        clearTravelHistory();

                        dialogRes = new Dialog(TravelHistoryFrom.this);
                        dialogRes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogRes.setCancelable(false);
                        dialogRes.setTitle("User Settings");
                        dialogRes.setContentView(R.layout.dialog_res);
                        dialogRes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogRes.show();


                        TextView btnOK=dialogRes.findViewById(R.id.btnOK);
                        btnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogRes.dismiss();
                                Intent intent = new Intent(TravelHistoryFrom.this, UserLoginActivity.class);
                                //intent.putExtra("details", customerRegistrationDto);
                                startActivity(intent);
                                finish();

                                //Toast.makeText(TravelHistoryFrom.this, "Registration completed", Toast.LENGTH_SHORT).show();
                            }
                        });


/*                        Intent intent = new Intent(TravelHistoryFrom.this, UserLoginActivity.class);
                        //intent.putExtra("details", customerRegistrationDto);
                        startActivity(intent);

                        Toast.makeText(TravelHistoryFrom.this, "Registration completed", Toast.LENGTH_SHORT).show();*/

                    } else {
                        Toast.makeText(TravelHistoryFrom.this, "Something went wrong, Please try agian.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TravelHistoryFrom.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(TravelHistoryFrom.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCountries() {

        setProgressDialog();
        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        Call<String> call = null;
        call = apiService.GetCountries();

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                dialog.dismiss();

                List<String> list = new ArrayList<>();

                if (response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(String.valueOf(jsonArray.get(i)));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterCountries =
                            new ArrayAdapter<String>(TravelHistoryFrom.this, android.R.layout.simple_spinner_dropdown_item,
                                    list);
                    countryDropDown.setAdapter(adapterCountries);

                } else {
                    Toast.makeText(TravelHistoryFrom.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(TravelHistoryFrom.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GetStatesbasedonCountry(String selectedCountry) {

        setProgressDialog();
        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        Call<String> call = null;
        call = apiService.GetStatesbasedonCountry(selectedCountry);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                dialog.dismiss();

                List<String> list = new ArrayList<>();
                if (response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(String.valueOf(jsonArray.get(i)));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterStates =
                            new ArrayAdapter<String>(TravelHistoryFrom.this, android.R.layout.simple_spinner_dropdown_item,
                                    list);
                    stateDropDown.setAdapter(adapterStates);

                } else {
                    Toast.makeText(TravelHistoryFrom.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(TravelHistoryFrom.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GetCitybasedonState(String selectedState) {

        setProgressDialog();
        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        Call<String> call = null;
        call = apiService.GetCitybasedonState(selectedState);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                dialog.dismiss();

                List<String> list = new ArrayList<>();
                if (response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(String.valueOf(jsonArray.get(i)));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterCities =
                            new ArrayAdapter<String>(TravelHistoryFrom.this, android.R.layout.simple_spinner_dropdown_item,
                                    list);
                    cityDropDown.setAdapter(adapterCities);

                } else {
                    Toast.makeText(TravelHistoryFrom.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(TravelHistoryFrom.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void clearTravelHistory() {
        countryDropDown.setAdapter(adapterCountries);

        stateDropDown.setAdapter(adapterStates);

        cityDropDown.setAdapter(adapterCities);
        cityDropDown.setVisibility(View.GONE);
        txtInptCity.setVisibility(View.VISIBLE);
        addressTravelHistory.setText("");
        etPinCodeTravelHistory.setText("");
    }


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};


    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    public void setProgressDialog() {

        int llPadding = 5;
        LinearLayout ll = new LinearLayout(TravelHistoryFrom.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(TravelHistoryFrom.this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(TravelHistoryFrom.this);
        //tvText.setText("Please wait..");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(TravelHistoryFrom.this);
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
