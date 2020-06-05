package com.myapp.nfcapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.myapp.nfcapplication.Interface.ApiInterface;
import com.myapp.nfcapplication.Pojo.CustomerRegistration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.M)
public class WriteTagActivity extends AppCompatActivity {

    public static final String TAG = WriteTagActivity.class.getSimpleName();
    private NfcAdapter mNfcAdapter;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    TextView txtData;

    TextView txtName, txtUID, txtPhone;
    private int customerId;
    LinearLayout llScanQR, llScanNFC, llDetails;
    EditText etAadharNumberPassport;
    ImageView ivSearch;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    Gson gson;

    Dialog dialogNFCWrite;
    Dialog dialog;
    Toolbar toolBar;
    IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

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

        txtName = findViewById(R.id.txtName);
        txtUID = findViewById(R.id.txtUID);
        txtPhone = findViewById(R.id.txtPhone);

        llDetails = findViewById(R.id.llDetails);
        llScanNFC = findViewById(R.id.llScanNFC);
        llScanQR = findViewById(R.id.llScanQR);

        ivSearch = findViewById(R.id.ivSearch);
        etAadharNumberPassport = findViewById(R.id.etAadharNumberPassport);

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setCaptureActivity(CaptureActivityPortrait.class);

        llDetails.setVisibility(View.GONE);

        llScanNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogNFCWrite = new Dialog(WriteTagActivity.this);
                dialogNFCWrite.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogNFCWrite.setCancelable(true);
                dialogNFCWrite.setContentView(R.layout.fragment_read);
                dialogNFCWrite.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialogNFCWrite.show();
            }
        });

        llScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initiating the qr code scan
                qrScan.initiateScan();
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etAadharNumberPassport.getText().toString().isEmpty())
                    getDetails();
                else
                    Toast.makeText(WriteTagActivity.this, "Enter Aadhar/Passport number to proceed", Toast.LENGTH_SHORT).show();
                //llDetails.setVisibility(View.VISIBLE);
            }
        });


        gson = new Gson();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

       /* if (getIntent().getSerializableExtra("details") != null) {

            customerRegistration = (CustomerRegistration) getIntent().getSerializableExtra("details");
            txtName.setText(customerRegistration.getFirstName());
            txtUID.setText(customerRegistration.getAadharNumber());
            txtAddress.setText(customerRegistration.getAddressLine1());
            txtCity.setText(customerRegistration.getCity());
            txtPhone.setText(customerRegistration.getMobileNo1());
            txtState.setText(customerRegistration.getState());
            txtPinCode.setText(customerRegistration.getPinCode());
            customerId = customerRegistration.getCustomerID();



        }*/

/*        Intent intent = new Intent(WriteTagActivity.this, ForegroundService.class);
        intent.setAction("ACTION_START_FOREGROUND_SERVICE");
        startService(intent);*/


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
                updateQRCode(result.getContents());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    public void updateQRCode(String qrCode) {

        setProgressDialog();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setCustomerID(customerId);
        customerRegistrationDto.setQRCode(qrCode);

        Call<String> call = null;
        call = apiService.UpdateQRCode(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerDto;
                dialog.dismiss();
                if (response.body() != null) {

                    customerDto = new Gson().fromJson(response.body(), CustomerRegistration.class);

                    customerId = customerDto.getCustomerID();
                    MaterialDialogUtils.showUploadSuccessDialog(WriteTagActivity.this, "Done");

                } else {
                    Toast.makeText(WriteTagActivity.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(WriteTagActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDetails() {

        setProgressDialog();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        CustomerRegistration customerRegistrationDto = new CustomerRegistration();

        customerRegistrationDto.setAadharNumber(etAadharNumberPassport.getText().toString());

        Call<String> call = null;
        call = apiService.GETCustomerData(customerRegistrationDto);

        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                CustomerRegistration customerDto;
                dialog.dismiss();
                if (response.body() != null) {

                    customerDto = new Gson().fromJson(response.body(), CustomerRegistration.class);

                    if (customerDto.getCustomerID() != 0) {


                        if (customerDto.getIsRegistrationCompleted() != 0) {


                                txtName.setText(customerDto.getFirstName());
                                txtUID.setText(customerDto.getAadharNumber());
                                txtPhone.setText(customerDto.getMobileNo1());
                                customerId = customerDto.getCustomerID();
                                llDetails.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(WriteTagActivity.this, "Registration not completed", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(WriteTagActivity.this, "Aadhar/Passport number not registered.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WriteTagActivity.this, "Something went wrong..!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(WriteTagActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setProgressDialog() {

        int llPadding = 5;
        LinearLayout ll = new LinearLayout(WriteTagActivity.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(WriteTagActivity.this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(WriteTagActivity.this);
        //tvText.setText("Please wait..");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(WriteTagActivity.this);
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
            } catch (NullPointerException e) {
                //
            }

            Log.d(TAG, "onNewIntent: " + intent.getAction());
            List<String> techLists = Arrays.asList(tag.getTechList());
            Log.d(TAG, "onNewIntent: " + techLists.toString());
            String serialNumber = bytesToHex(tag.getId());
            Log.d(TAG, "onNewIntent: " + serialNumber);

            if (tag != null) {
                Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
                Ndef ndef = Ndef.get(tag);
                if (dialogNFCWrite != null && dialogNFCWrite.isShowing()) {
                    if (ndef == null) {
                        writeTag(ndefMessage, tag);
                    } else {
                        writeToNfc(ndef, String.valueOf(customerId));
                    }
                }
            }
        }
    }


    private void writeToNfc(Ndef ndef, String message) {

        txtData.setText(getString(R.string.message_write_progress));
        if (ndef != null) {
            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", message.getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                //Write Successful
                txtData.setText(getString(R.string.message_write_success));
                txtData.setTextColor(Color.green(R.color.green));
                dialogNFCWrite.dismiss();

                MaterialDialogUtils.showUploadSuccessDialog(WriteTagActivity.this, "Activated");

            } catch (Exception e) {
                dialogNFCWrite.dismiss();
                MaterialDialogUtils.showUploadErrorDialog(WriteTagActivity.this, "Error");
                //txtData.setText(getString(R.string.message_write_error));
            }
        } else {
            dialogNFCWrite.dismiss();
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
                writeToNfc(ndef, String.valueOf(customerId) + txtName.getText().toString());
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
                                    Toast.makeText(WriteTagActivity.this, location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(WriteTagActivity.this, mLastLocation.getLatitude() + " : " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
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
    }


}
