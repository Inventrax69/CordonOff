package com.myapp.nfcapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.myapp.nfcapplication.Interface.ApiInterface;
import com.myapp.nfcapplication.Pojo.CustomerRegistration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.M)
public class UserActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = UserActivity.class.getSimpleName();
    private NfcAdapter mNfcAdapter;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    TextView txtData, txtLat, txtLong, txtMyLocation, txtLastUpdate, txtRegDate, txtCDaysPending,
            txtViolationsRecorded, scanNFCTag, txtUserName, txtPhoneNumber, tvQuarentineRange;
    TextView scanQrCode;
    TextView setLocation;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String address, city, state, zip, country;
    String value = "1";
    IntentIntegrator qrScan;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    boolean isScan = false;
    Dialog dialog;
    Dialog progressDialog;
    int customerID, toNoOfdaysConfined = 14;
    ImageView fText;
    TextView btnBand, btnlocation;
    String qrcode;
    int isHomeQuarantine;
    CardView cartviewNFCScan;
    Dialog dialogNFCRead;
    Dialog dialogUserSetting;
    SharedPreferences prefs;
    MapFragment mapFragment;
    AlertDialog.Builder builder;
    Date currentTime = null, date = null;
    Button btnLogOut, btnClose, btnSave;
    EditText etDistanceInMeters, etTimeInMinutes;
    LinearLayout linearDialog;

    Toolbar toolBar;
    ActionBar actionBar;

    private void startAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

        int configuredTime = prefs.getInt(KeyValues.CONFIGURED_TIME,1);
        int violationValue = prefs.getInt(KeyValues.VIOLATION_VALUE,0);

        // Configure time as per the requirement
        if(violationValue<2){
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, 60000, pendingIntent);
        }else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, configuredTime*60000, pendingIntent);
        }

        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, 60000, pendingIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txtUserName = findViewById(R.id.txtUserName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtData = findViewById(R.id.txtData);
        txtLat = findViewById(R.id.txtLat);
        txtLong = findViewById(R.id.txtLong);
        txtMyLocation = findViewById(R.id.txtMyLocation);
        txtLastUpdate = findViewById(R.id.txtLastUpdate);
        txtRegDate = findViewById(R.id.txtRegDate);
        txtCDaysPending = findViewById(R.id.txtCDaysPending);
        txtViolationsRecorded = findViewById(R.id.txtViolationsRecorded);

        scanQrCode = findViewById(R.id.scanQrCode);
        setLocation = findViewById(R.id.setLocation);
        scanNFCTag = findViewById(R.id.scanNFCTag);

        cartviewNFCScan = findViewById(R.id.cartviewNFCScan);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //initializing scan object
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setCaptureActivity(CaptureActivityPortrait.class);

        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initiating the qr code scan
                qrScan.initiateScan();
            }
        });

        toolBar = findViewById(R.id.toolBar);

        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();


        prefs = getSharedPreferences("CordonOff", MODE_PRIVATE);
        isHomeQuarantine = prefs.getInt(KeyValues.IS_HOME_QUARANTINE, 0);
        customerID = prefs.getInt(KeyValues.CUSTOMERID, 0);

        txtUserName.setText(prefs.getString(KeyValues.NAME_OF_THE_PERSON, ""));
        txtPhoneNumber.setText(prefs.getString(KeyValues.MOBILE_ONE, ""));

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value = "1";
                getLastLocation(value);
            }
        });

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tvQuarentineRange = findViewById(R.id.tvQuarentineRange);
        tvQuarentineRange.setText(prefs.getInt(KeyValues.CONFIGURED_GEO_LOCATION,50)+" Meters");

        //checkInternetConnection(UserActivity.this);
/*
        txtRegDate.setText("20-05-2020");
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatIn = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = formatIn.parse(txtRegDate.getText().toString());
            // System.out.println(date);
            txtCDaysPending.setText("" + (14 - getDaysDiff(currentTime, date)) + " Days");
        } catch (ParseException e) {
            // e.printStackTrace();
        }


        int violationValue = prefs.getInt(KeyValues.VIOLATION_VALUE, 0);
        String lastUpdate = prefs.getString(KeyValues.LAST_UPDATE, "");
        String myAddress = prefs.getString(KeyValues.HOME_ADDRESS, "");
        String homeLat = prefs.getString(KeyValues.LATITUDE, "");
        String homeLong = prefs.getString(KeyValues.LONGITUDE, "");
        txtViolationsRecorded.setText("" + violationValue);

        if (isHomeQuarantine == 0) {
            setLocation.setVisibility(View.VISIBLE);
        } else {
            setLocation.setVisibility(View.GONE);
            txtLat.setText(homeLat);
            txtLong.setText(homeLong);
            txtMyLocation.setText(myAddress);
            startAlarm();
        }
*/

     /*   if(isHomeQ){
            setLocation.setVisibility(View.GONE);
            txtLat.setText(homeLat);
            txtLong.setText(homeLong);
            txtMyLocation.setText(myAddress);
        }
        txtLastUpdate.setText(lastUpdate);
*/

        initNFC();

        txtRegDate.setText(prefs.getString(KeyValues.CREATED_DATE, ""));
        // txtRegDate.setText("28-05-2020 12:00:00"); 5/28/2020 12:00:00 AM
        currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatIn = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        // SimpleDateFormat formatIn = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        try {
            date = formatIn.parse(txtRegDate.getText().toString());
            // txtCDaysPending.setText("" + (toNoOfdaysConfined - getDaysDiff(currentTime, date)) + " Days");
        } catch (ParseException e) {
            // e.printStackTrace();
        }

        int remainDays = 0, completedDays = 0;
        if ((toNoOfdaysConfined - getDaysDiff(currentTime, date)) > 0) {
            remainDays = toNoOfdaysConfined - getDaysDiff(currentTime, date);
            completedDays = toNoOfdaysConfined - remainDays;
        }

        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        ArrayList<PieEntry> entries = new ArrayList<>();
        if ((toNoOfdaysConfined - getDaysDiff(currentTime, date)) > 0) {
            entries.add(new PieEntry((float) completedDays, ""));
            entries.add(new PieEntry((float) remainDays, ""));
        } else {
            entries.add(new PieEntry(100f, ""));
            entries.add(new PieEntry(0f, ""));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(getColor(R.color.orange), getColor(R.color.orange_red));
        PieData pieData = new PieData(pieDataSet);
        if ((toNoOfdaysConfined - getDaysDiff(currentTime, date)) > 0) {
            pieChart.setCenterText(14 - getDaysDiff(currentTime, date) + " \n Days");
        } else {
            pieChart.setCenterText(0 + " \n Days");
        }

        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextColor(Color.BLUE);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieData.setDrawValues(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateXY(2000, 2000);
        pieChart.invalidate();

        int violationCount = prefs.getInt(KeyValues.VIOLATION_COUNT, 0);
        String lastUpdate = prefs.getString(KeyValues.LAST_UPDATE, "");
        String myAddress = prefs.getString(KeyValues.HOME_ADDRESS, "");
        String homeLat = prefs.getString(KeyValues.LATITUDE, "");
        String homeLong = prefs.getString(KeyValues.LONGITUDE, "");
        txtViolationsRecorded.setText("" + violationCount);

        if (isHomeQuarantine == 0) {
            setLocation.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.GONE);
        } else {
            mapFragment.getView().setVisibility(View.VISIBLE);
            setLocation.setVisibility(View.GONE);
            txtLat.setText(homeLat);
            txtLong.setText(homeLong);
            if (myAddress.isEmpty()) {
                getAddress(Double.parseDouble(homeLat), Double.parseDouble(homeLong));
            } else {
                txtMyLocation.setText(myAddress);
            }
            startAlarm();
        }

        if (!lastUpdate.equals("") && !lastUpdate.isEmpty())
            txtLastUpdate.setText(lastUpdate);
        else
            txtLastUpdate.setText("No update Yet");


        if (getDaysDiff(currentTime, date) <= 2) {
            setLocation.setVisibility(View.VISIBLE);
        }


        //////////////

        //////////////

        fText = findViewById(R.id.fText);
        btnBand = findViewById(R.id.btnBand);
        btnlocation = findViewById(R.id.btnlocation);
        linearDialog = findViewById(R.id.linearDialog);

        TextDrawable drawable = TextDrawable.builder().buildRound(txtUserName.getText().toString().substring(0, 1), Color.parseColor("#FFFFFF"));

        fText.setImageDrawable(drawable);


        int isBandActive = prefs.getInt(KeyValues.IS_BAND_ACTIVE, 0);
        int isLocationExceed = prefs.getInt(KeyValues.IS_LOCATION_EXCEED, 0);

        if (isBandActive == 1) {
            btnBand.setText("  Inactive  ");
            btnBand.setBackgroundDrawable(getResources().getDrawable(R.drawable.band_inactive_1));
        } else {
            btnBand.setText("  Active  ");
            btnBand.setBackgroundDrawable(getResources().getDrawable(R.drawable.band_active_1));
        }

        if (isLocationExceed == 1) {
            // btnlocation.setBackgroundDrawable(getResources().getDrawable(R.drawable.band_inactive));
            btnlocation.setText("(Away Home)");
            btnlocation.setTextColor(getColor(R.color.dark_red));
        } else {
            // btnlocation.setBackgroundDrawable(getResources().getDrawable(R.drawable.band_active));
            btnlocation.setText("(In-Home)");
            btnlocation.setTextColor(getColor(R.color.green));
        }


        qrcode = prefs.getString(KeyValues.QRCODE, "");


        cartviewNFCScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
                NfcAdapter adapter = manager.getDefaultAdapter();
                if (adapter != null && adapter.isEnabled()) {

                    dialogNFCRead = new Dialog(UserActivity.this);
                    dialogNFCRead.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogNFCRead.setCancelable(true);
                    dialogNFCRead.setContentView(R.layout.fragment_write);
                    dialogNFCRead.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogNFCRead.show();

                } else {
                    Toast.makeText(UserActivity.this, "Doesnt support NFC", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /*Intent intent = new Intent(UserActivity.this, ForegroundService.class);
        intent.setAction("ACTION_START_FOREGROUND_SERVICE");
        startService(intent);*/

        linearDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Uncomment the below code to Set the message and title from the strings.xml file
                // builder.setMessage("") .setTitle(R.string.dialog_title);
/*                builder = new AlertDialog.Builder(UserActivity.this);
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                                editor.putInt(KeyValues.IS_LOGIN, 0);
                                editor.apply();
                                startActivity(new Intent(UserActivity.this, UserLoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();*/


                ////////////////////

                dialogUserSetting = new Dialog(UserActivity.this);
                dialogUserSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogUserSetting.setCancelable(true);
                dialogUserSetting.setTitle("User Settings");
                dialogUserSetting.setContentView(R.layout.dialog_config);
                dialogUserSetting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogUserSetting.show();

                btnLogOut = dialogUserSetting.findViewById(R.id.btnLogOut);
                btnClose = dialogUserSetting.findViewById(R.id.btnClose);
                btnSave = dialogUserSetting.findViewById(R.id.btnSave);

                etDistanceInMeters = dialogUserSetting.findViewById(R.id.etDistanceInMeters);
                etTimeInMinutes = dialogUserSetting.findViewById(R.id.etTimeInMinutes);

                btnLogOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogUserSetting.dismiss();
                        SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                        editor.putInt(KeyValues.IS_LOGIN, 0);
                        editor.apply();
                        startActivity(new Intent(UserActivity.this, UserLoginActivity.class));
                        finish();

                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogUserSetting.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!etTimeInMinutes.getText().toString().isEmpty() && !etDistanceInMeters.getText().toString().isEmpty()){
                            if(!etTimeInMinutes.getText().toString().equalsIgnoreCase("0") && !etDistanceInMeters.getText().toString().equalsIgnoreCase("0")) {
                                dialogUserSetting.dismiss();
                                tvQuarentineRange.setText(prefs.getInt(KeyValues.CONFIGURED_GEO_LOCATION,50)+" Meters");
                                SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                                editor.putInt(KeyValues.CONFIGURED_GEO_LOCATION, Integer.parseInt(etDistanceInMeters.getText().toString()));
                                editor.putInt(KeyValues.CONFIGURED_TIME, Integer.parseInt(etTimeInMinutes.getText().toString()));
                                editor.apply();
                            }else {
                                Toast.makeText(UserActivity.this, "Please enter valid details.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(UserActivity.this, "Please enter details.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

        if (mNfcAdapter == null) {

            Toast.makeText(this, "NFC not supported", Toast.LENGTH_SHORT).show();
            //initiating the qr code scan
            //qrScan.initiateScan();

        } else if (!mNfcAdapter.isEnabled()) {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            } else {
                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            }

            startActivity(intent);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_sync_info:

                //Toast.makeText(this, "sync", Toast.LENGTH_SHORT).show();

                getDetails();
                break;

            case R.id.action_options:
                dialogUserSetting = new Dialog(UserActivity.this);
                dialogUserSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogUserSetting.setCancelable(true);
                dialogUserSetting.setTitle("User Settings");
                dialogUserSetting.setContentView(R.layout.dialog_config);
                dialogUserSetting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogUserSetting.show();

                btnLogOut = dialogUserSetting.findViewById(R.id.btnLogOut);
                btnClose = dialogUserSetting.findViewById(R.id.btnClose);
                btnSave = dialogUserSetting.findViewById(R.id.btnSave);

                etDistanceInMeters = dialogUserSetting.findViewById(R.id.etDistanceInMeters);
                etTimeInMinutes = dialogUserSetting.findViewById(R.id.etTimeInMinutes);

                btnLogOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogUserSetting.dismiss();
                        SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                        editor.putInt(KeyValues.IS_LOGIN, 0);
                        editor.apply();
                        startActivity(new Intent(UserActivity.this, UserLoginActivity.class));
                        finish();

                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogUserSetting.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!etTimeInMinutes.getText().toString().isEmpty() && !etDistanceInMeters.getText().toString().isEmpty()) {
                            if (!etTimeInMinutes.getText().toString().equalsIgnoreCase("0") && !etDistanceInMeters.getText().toString().equalsIgnoreCase("0")) {
                                dialogUserSetting.dismiss();
                                tvQuarentineRange.setText(prefs.getInt(KeyValues.CONFIGURED_GEO_LOCATION, 50) + " Meters");
                                SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                                editor.putInt(KeyValues.CONFIGURED_GEO_LOCATION, Integer.parseInt(etDistanceInMeters.getText().toString()));
                                editor.putInt(KeyValues.CONFIGURED_TIME, Integer.parseInt(etTimeInMinutes.getText().toString()));
                                editor.apply();
                            } else {
                                Toast.makeText(UserActivity.this, "Please enter valid details.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserActivity.this, "Please enter details.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                //Toast.makeText(this, "options", Toast.LENGTH_SHORT).show();
                break;


            case R.id.action_multilang:

                Toast.makeText(this, "multi lang", Toast.LENGTH_SHORT).show();
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    private boolean checkInternetConnection(Activity activity) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = null;
            if (connectivityManager == null) {
                return false;
            } else {
                network = connectivityManager.getActiveNetwork();
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities == null) {
                    return false;
                }
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    return true;
                }
                if ( networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    return true;
                }

            }
        } else {
            if (connectivityManager == null) {
                return false;
            }
            if (connectivityManager.getActiveNetworkInfo() == null) {
                return false;
            }
            return connectivityManager.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public void getDetails() {

        setProgressDialog();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setAadharNumber(prefs.getString(KeyValues.AADHAR_NUMBER,""));

        Call<String> call = null;
        call = apiService.GETCustomerData(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerDto;
                progressDialog.dismiss();
                if (response.body() != null) {

                    customerDto = new Gson().fromJson(response.body(), CustomerRegistration.class);

                    if(!customerDto.getQRCode().isEmpty() || customerDto.getIsMobileNFCEnabled()!=0) {

                        txtViolationsRecorded.setText(customerDto.getVialotionCount()+"");

                    }else {
                        Toast.makeText(UserActivity.this, "Tag activation not yet done. Please do active tag.", Toast.LENGTH_SHORT).show();
                    }

                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UserActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public int getDaysDiff(Date datefrom, Date dateto) {

        long diff = datefrom.getTime() - dateto.getTime();

        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));

        return numOfDays;

    }

    private void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }//bytesToHex()

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equalsIgnoreCase(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = null;
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if ((rawMessages != null) && (rawMessages.length > 0)) {
                ndefMessage = (NdefMessage) rawMessages[0];
            }
            try {
                ndefMessage.getRecords();
            } catch (Exception e) {
                //
            }

            Log.d(TAG, "onNewIntent: " + intent.getAction());
            List<String> techLists = Arrays.asList(tag.getTechList());
            Log.d(TAG, "onNewIntent: " + techLists.toString());
            String serialNumber = bytesToHex(tag.getId());
            Log.d(TAG, "onNewIntent: " + serialNumber);

            /*if (dialogNFCRead != null && dialogNFCRead.isShowing()) {
                if (tag != null) {
                    Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
                    Ndef ndef = Ndef.get(tag);
                    if (ndef == null) {
                        writeTag(ndefMessage, tag);
                        return;
                    } else {
                        readFromNFC(ndef);
                    }
                }
            }*/

            if (tag != null) {
                Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {
                    writeTag(ndefMessage, tag);
                    return;
                } else {
                    readFromNFC(ndef);
                }
            }

        }
    }

    private void readFromNFC(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String message = new String(ndefMessage.getRecords()[0].getPayload());
            Log.d(TAG, "readFromNFC: " + message);
            if (message.equalsIgnoreCase("" + customerID)) {
                setUpdate();
                SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                editor.putInt(KeyValues.IS_BAND_ACTIVE, 0);
                editor.apply();
                int isBandActive = prefs.getInt(KeyValues.IS_BAND_ACTIVE, 0);
                if (isBandActive == 1) {
                    btnBand.setText("  Inactive  ");
                    btnBand.setBackgroundDrawable(getResources().getDrawable(R.drawable.band_inactive_1));
                } else {
                    btnBand.setText("  Active  ");
                    btnBand.setBackgroundDrawable(getResources().getDrawable(R.drawable.band_active_1));
                }
                txtData.setText(message);
            }

            if (dialogNFCRead != null && dialogNFCRead.isShowing())
                dialogNFCRead.dismiss();
            MaterialDialogUtils.showUploadSuccessDialog(UserActivity.this, "Success");
            ndef.close();
        } catch (IOException | FormatException | NullPointerException e) {
            //e.printStackTrace();

            if (dialogNFCRead != null && dialogNFCRead.isShowing())
                dialogNFCRead.dismiss();
            MaterialDialogUtils.showUploadErrorDialog(UserActivity.this, "Falied");
            Log.v(TAG, "ddd");
        }

    }

    public boolean writeTag(NdefMessage ndefMessage, Tag tag) {

        try {

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                NdefFormatable format = NdefFormatable.get(tag);
                format.connect();
                format.format(ndefMessage);
                if (format.isConnected()) {
                    format.close();
                }
            } else {
                readFromNFC(ndef);
            }

            return true;
        } catch (Exception e) {
            Log.d("WriteTextActivity", e.toString());
        }
        return false;
    }


    //
    // Location details
    // Getting Lat location
    // Getting Long location
    //

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(UserActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            zip = addresses.get(0).getPostalCode();
            country = addresses.get(0).getCountryName();

            txtMyLocation.setText(city + " , " + state + " , " + zip);


            sendLocation(latitude, longitude);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void sendLocation(Double latitude, Double longitude) {

        setProgressDialog();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setLatitude(latitude + "");
        customerRegistrationDto.setLongitude(longitude + "");
        customerRegistrationDto.setCustomerID(customerID);

        Call<String> call = null;
        call = apiService.SetHomeQuarintine(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerDto;
                progressDialog.dismiss();
                if (response.body() != null) {

                    customerDto = new Gson().fromJson(response.body(), CustomerRegistration.class);
                    if (customerDto.getCustomerID() != 0) {
                        SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                        editor.putString(KeyValues.HOME_ADDRESS, city + " , " + state + " , " + zip);
                        editor.putString(KeyValues.LATITUDE, "" + latitude);
                        editor.putString(KeyValues.LONGITUDE, "" + longitude);
                        editor.putInt(KeyValues.IS_HOME_QUARANTINE, 1);
                        editor.apply();
                        mapFragment.getView().setVisibility(View.VISIBLE);
                        setLocation.setVisibility(View.GONE);
                        txtLat.setText("" + latitude);
                        txtLong.setText("" + longitude);
                        String myAddress = city + " , " + state + " , " + zip;
                        if (myAddress.isEmpty()) {
                            getAddress(Double.parseDouble("" + latitude), Double.parseDouble("" + longitude));
                        } else {
                            txtMyLocation.setText(myAddress);
                        }
                        startAlarm();
                        mapFragment.getMapAsync(UserActivity.this);
                        if (getDaysDiff(currentTime, date) <= 2) {
                            setLocation.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    Toast.makeText(UserActivity.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UserActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setProgressDialog() {

        int llPadding = 5;
        LinearLayout ll = new LinearLayout(UserActivity.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(UserActivity.this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(UserActivity.this);
        //tvText.setText("Please wait..");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setCancelable(true);
        builder.setView(ll);

        progressDialog = builder.create();
        progressDialog.show();
        Window window = progressDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(progressDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            progressDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void getDistance(double lat1, double lon1, double lat2, double lon2) {

        Location loc1 = new Location("");

        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2);

        // String.format("%.2f", distanceInMeters / 1000), VIla >dis

        // Vialation
        //

        int isBandActive = prefs.getInt(KeyValues.IS_BAND_ACTIVE, 0);

        Toast.makeText(this, "" + String.format("%.2f", distanceInMeters), Toast.LENGTH_SHORT).show();

        float violationDistance = prefs.getInt(KeyValues.CONFIGURED_GEO_LOCATION,50);

        if (violationDistance < distanceInMeters) {
            SharedPreferences.Editor editor = UserActivity.this.getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
            editor.putInt(KeyValues.IS_LOCATION_EXCEED, 1);
            editor.apply();
            btnlocation.setText("(Away Home)");
            btnlocation.setTextColor(getColor(R.color.dark_red));

            if (isBandActive == 1)
                submitViolationDetails(UserActivity.this, customerID, true, KeyValues.Inactive_Tag, true, KeyValues.GEO_FENCE_VIOLATION);
            else
                submitViolationDetails(UserActivity.this, customerID, false, KeyValues.Inactive_Tag, true, KeyValues.GEO_FENCE_VIOLATION);
        } else {
            SharedPreferences.Editor editor = UserActivity.this.getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
            editor.putInt(KeyValues.IS_LOCATION_EXCEED, 0);
            editor.apply();
            btnlocation.setText("(In-Home)");
            btnlocation.setTextColor(getColor(R.color.green));

            if (isBandActive == 1)
                submitViolationDetails(UserActivity.this, customerID, true, KeyValues.Inactive_Tag, false, KeyValues.GEO_FENCE_VIOLATION);
            else
                submitViolationDetails(UserActivity.this, customerID, false, KeyValues.Inactive_Tag, false, KeyValues.GEO_FENCE_VIOLATION);
        }

    }

    public void submitViolationDetails(Context context, int customerId, boolean IsBandViolation, String violationType, boolean IsGeoViolation, String GEOVialotion) {


        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setBandViolation(IsBandViolation);
        customerRegistrationDto.setVialotionType(violationType);
        customerRegistrationDto.setGeoViolation(IsGeoViolation);
        customerRegistrationDto.setGEOVialotionType(GEOVialotion);
        customerRegistrationDto.setCustomerID(customerId);

        Call<String> call = null;
        call = apiService.SetVialotions(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerDto;

                if (response.body() != null) {

                    customerDto = new Gson().fromJson(response.body(), CustomerRegistration.class);
                    if (customerDto.getVialotionID() > 0) {

                    }

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getLastLocation(String value) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //  TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }

                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    if (value.equalsIgnoreCase("1")) {
                                        txtLong.setText("" + location.getLongitude());
                                        txtLat.setText("" + location.getLatitude());
                                        getAddress(location.getLatitude(), location.getLongitude());
                                        startAlarm();
                                    } else {
                                        try {
                                            getDistance(location.getLatitude(), location.getLongitude(), Double.parseDouble(txtLat.getText().toString()), Double.parseDouble(txtLong.getText().toString()));
                                        } catch (Exception e) {

                                        }
                                    }
                                }
                            }
                        }
                );
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Location mLastLocation = locationResult.getLastLocation();
            if (value.equalsIgnoreCase("1")) {
                txtLong.setText("" + mLastLocation.getLongitude());
                txtLat.setText("" + mLastLocation.getLatitude());
                getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                startAlarm();
            } else {
                try {
                    getDistance(mLastLocation.getLatitude(), mLastLocation.getLongitude(), Double.parseDouble(txtLat.getText().toString()), Double.parseDouble(txtLong.getText().toString()));
                } catch (Exception e) {
                    //
                }
            }
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(value);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                //txtLocation.setText(result.getContents());
                if (result.getContents().equalsIgnoreCase(qrcode)) {
                    setUpdate();
                    MaterialDialogUtils.showUploadSuccessDialog(UserActivity.this, "Success");
                } else {
                    MaterialDialogUtils.showUploadErrorDialog(UserActivity.this, "Falied");
                    Toast.makeText(this, "Wrong QR Code Scanned", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void setUpdate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SharedPreferences.Editor editor = getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
        editor.putInt(KeyValues.VIOLATION_VALUE, 0);
        editor.putString(KeyValues.LAST_UPDATE, fmtOut.format(currentTime));
        editor.apply();

        value = "2";
        getLastLocation(value);
        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

        startAlarm();
        txtLastUpdate.setText(fmtOut.format(currentTime));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (isHomeQuarantine == 1) {
            double lat = Double.parseDouble(txtLat.getText().toString());
            double lang = Double.parseDouble(txtLong.getText().toString());

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lang))
                    .title(txtMyLocation.getText().toString()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lang), 15));

            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());

            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lang))      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

}
