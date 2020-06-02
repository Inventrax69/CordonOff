package com.myapp.nfcapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.myapp.nfcapplication.Interface.ApiInterface;
import com.myapp.nfcapplication.Pojo.CustomerRegistration;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.M)
public class RegistrationActivity extends AppCompatActivity {

    public static final String TAG = RegistrationActivity.class.getSimpleName();
    private NfcAdapter mNfcAdapter;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    TextView txtData;
    TextInputEditText etFullName, etAadharNumber, etPhoneOne;
    Button btnSubmit, btnClear;
    CheckBox cbIsSmartPhone, cbIsNFCEnabled;
    ImageView scanAadhar;

    int isSmartPhone = 0, isNFCEnabled = 0;
    int PERMISSION_ID = 44;
    // FusedLocationProviderClient mFusedLocationClient;
    private ArrayAdapter<String> adapterStates;
    private ArrayAdapter<String> adapterCities;

    private Toolbar toolBar;

    private int customerID;
    private String UID = "";

    Dialog dialog;

    Gson gson;

    IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

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
        btnSubmit = findViewById(R.id.btnSubmit);
        btnClear = findViewById(R.id.btnClear);

        etFullName = findViewById(R.id.etFullName);
        etAadharNumber = findViewById(R.id.etAadharNumber);
        etPhoneOne = findViewById(R.id.etPhoneOne);

        scanAadhar = findViewById(R.id.scanAadhar);

        cbIsNFCEnabled = findViewById(R.id.cbIsNFCEnabled);
        cbIsSmartPhone = findViewById(R.id.cbIsSmartPhone);


        gson = new Gson();

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setCaptureActivity(CaptureActivityPortrait.class);

        // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

/*        Intent intent = new Intent(RegistrationActivity.this, ForegroundService.class);
        intent.setAction("ACTION_START_FOREGROUND_SERVICE");
        startService(intent);*/

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getLastLocation();
                if (etAadharNumber.getText().toString().isEmpty() || etFullName.getText().toString().isEmpty()
                        || etPhoneOne.getText().toString().isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                } else {
                    submitDetails();

                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getLastLocation();
                etPhoneOne.setText("");
                etFullName.setText("");
                etAadharNumber.setText("");
            }
        });

        cbIsNFCEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    isNFCEnabled = 1;
                else
                    isNFCEnabled = 0;
            }
        });

        cbIsSmartPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    isSmartPhone = 1;
                else
                    isSmartPhone = 0;
            }
        });

        scanAadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initiating the qr code scan
                qrScan.initiateScan();
            }
        });

        initNFC();

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
                xmlParser(result.getContents());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void xmlParser(String xml) {
        try {
            JSONObject xmlJSONObj = XML.toJSONObject(xml);
            if(XML.toJSONObject(xml).toString().contains("PrintLetterBarcodeData") ){
                JSONObject detailsObj = new JSONObject(XML.toJSONObject(xml).getString("PrintLetterBarcodeData"));
                etAadharNumber.setText(detailsObj.getString("uid"));
                etFullName.setText(detailsObj.getString("name"));

            }else if( XML.toJSONObject(xml).toString().contains("QDA")){
                JSONObject detailsObj = new JSONObject(XML.toJSONObject(xml).getString("QDA"));
                etAadharNumber.setText(detailsObj.getString("u"));
                etFullName.setText(detailsObj.getString("n"));
            }


           /*
            *
            Aadhar XML Json Without Encryption
            *
            *
            *
            *//*{"PrintLetterBarcodeData":{"uid":795XXXXXXXXX,"name":"ROY","gender":"M",
                    "yob":1995,"co":"S\/O Joshua","house":"1","street":"A Colony",
                    "lm":"Near Dominos","loc":"MVP","vtc":"Visakhapatnam","dist":"Visakhapatnam",
                    "state":"Andhra Pradesh","pc":530047}}*//*
             */

            /*
            *
            *
            *

            Aadhar XML Json With Encryption
            *
            *
            */
            /*{"n":"Yernena Lalithakumari","u":"xxxxxxxx5413","g":"F","d":"02-08-1996",
                    "a":"3-131,Pedda Veedhi,Gopem Peta,Srikakulam,Andhra Pradesh,532440","x":"",
                    "s":"fP4x+fITXRv1Qp0EwvVOOoJbe65UjR1e87+J1N15P56R+rYVSB9XEEJPrJL0kZLThuuw9ULTM2WKlfzF3dF+P1oRE5foQPVbOsS1L0++bN9oZ5Qf0CQJYTrndBlbzmNJfTkAkmzVgUcCsNcu3o8\/CwDhLVyw0t2lskj9p\/7xXf7DCpZqTot4vke4glbgl6GiKJXYNKticqNixpc01UaY2hRCiyhfTbrDjm73KdD63eoYwubqAaR3eoMEqPVaF1k1iaTVsMRlVW4gRoRHOW28iCDPtzfHlucUFuq8ai+k6++XFdcWCl9PJCO84uDRQfAdoaiF43HY0loSlhGr3jhuQA=="}*/

            //new JSONObject(XML.toJSONObject(xml).getString("PrintLetterBarcodeData")).getString("name");

        } catch (JSONException je) {
            //
        }
    }

    public void submitDetails() {

        setProgressDialog();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setAadharNumber(etAadharNumber.getText().toString());
        customerRegistrationDto.setFirstName(etFullName.getText().toString());
        customerRegistrationDto.setMobileNo1(etPhoneOne.getText().toString());
        customerRegistrationDto.setIsMobileNFCEnabled(isNFCEnabled);
        customerRegistrationDto.setIsMobileSmartPhone(isSmartPhone);

        Call<String> call = null;
        call = apiService.CreateCustomer(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerDto;
                dialog.dismiss();
                if (response.body() != null) {


                    customerDto = new Gson().fromJson(response.body(), CustomerRegistration.class);
                    customerID = customerDto.getCustomerID();
                    UID = customerDto.getAadharNumber();

                    if (customerID != -1) {
                        Intent intent = new Intent(RegistrationActivity.this, OTPActivity.class);
                        intent.putExtra(KeyValues.CUSTOMERID, customerID);
                        startActivity(intent);

                    } else {
                        Toast.makeText(RegistrationActivity.this, "Aadhar/Passport number is already registered", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(RegistrationActivity.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(RegistrationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void clearRegistrationDetails() {

        etFullName.setText("");
        etPhoneOne.setText("");
        etAadharNumber.setText("");
        cbIsSmartPhone.setChecked(false);
        cbIsNFCEnabled.setChecked(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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


    public void setProgressDialog() {

        int llPadding = 5;
        LinearLayout ll = new LinearLayout(RegistrationActivity.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(RegistrationActivity.this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(RegistrationActivity.this);
        //tvText.setText("Please wait..");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
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



/*    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equalsIgnoreCase(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = null;
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if ((rawMessages != null) && (rawMessages.length > 0)) {
                ndefMessage = (NdefMessage) rawMessages[0];
            }
            ndefMessage.getRecords();
            Log.d(TAG, "onNewIntent: " + intent.getAction());
            List<String> techLists = Arrays.asList(tag.getTechList());
            Log.d(TAG, "onNewIntent: " + techLists.toString());
            String serialNumber = bytesToHex(tag.getId());
            Log.d(TAG, "onNewIntent: " + serialNumber);

            if (tag != null) {
                Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {
                    writeTag(ndefMessage, tag);
                    return;
                } else {
                    writeToNfc(ndef, "anil");
                }
            }
        }
    }*/


 /*   private void writeToNfc(Ndef ndef, String message) {

        txtData.setText(getString(R.string.message_write_progress));
        if (ndef != null) {
            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", message.getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                //Write Successful
                txtData.setText(getString(R.string.message_write_success));

            } catch (IOException | FormatException e) {
                e.printStackTrace();
                txtData.setText(getString(R.string.message_write_error));
            } finally {
                //mProgress.setVisibility(View.GONE);
            }

        } else {
            txtData.setText("Failed to write the data");
            // mProgress.setVisibility(View.GONE);
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
                writeToNfc(ndef, "anil");
            }
            return true;
        } catch (Exception e) {
            Log.d("WriteTextActivity", e.toString());
        }
        return false;
    }

    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
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
                                    Toast.makeText(RegistrationActivity.this, location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    //
    // Location details
    // Getting Lat location
    // Getting Long location
    //

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
            Toast.makeText(RegistrationActivity.this, mLastLocation.getLatitude() + " : " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
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
                getLastLocation();
            }
        }
    }*/


}
