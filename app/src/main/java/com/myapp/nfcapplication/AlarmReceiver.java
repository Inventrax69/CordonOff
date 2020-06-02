package com.myapp.nfcapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.myapp.nfcapplication.Interface.ApiInterface;
import com.myapp.nfcapplication.Pojo.CustomerRegistration;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    MediaPlayer mp;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private void createNotificationChannel() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //int value= intent.getExtras().getInt("intent2",0);

        // Date currentTime = Calendar.getInstance().getTime();

        /*AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);*/

        /*if(currentTime.getHours() < 19 && currentTime.getHours() > 6){*/

        SharedPreferences.Editor editor = context.getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences("CordonOff", MODE_PRIVATE);

        int islogin = prefs.getInt(KeyValues.IS_LOGIN, 0);

        if (islogin == 1) {
            mp = MediaPlayer.create(context, R.raw.alarm);
            if (!mp.isPlaying()) {
                mp.start();
            }

            // mp.stop();


            int violationCount = prefs.getInt(KeyValues.VIOLATION_COUNT, 0);
            int customerId = prefs.getInt(KeyValues.CUSTOMERID, 0);
            int violationValue = prefs.getInt(KeyValues.VIOLATION_VALUE, 0);

            if (violationCount > 2) {
                editor.putInt(KeyValues.VIOLATION_COUNT, violationCount + 1);
                editor.putInt(KeyValues.IS_BAND_ACTIVE, 1);
                //api

                submitViolationDetails(context, customerId, true, KeyValues.Inactive_Tag, true, KeyValues.GEO_FENCE_VIOLATION);

                /*if(violationCount==4){
                    // distance > vialtion distance
                    submitViolationDetails(context,KeyValues.INACTIVE_TAG_VIOLATION,customerId);
                }*/

            }
            editor.putInt(KeyValues.VIOLATION_VALUE, violationValue + 1);
            editor.apply();

            Toast.makeText(context, "ALram", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID, "Foreground Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager manager = context.getSystemService(NotificationManager.class);
                manager.createNotificationChannel(serviceChannel);
            }

            String input = intent.getStringExtra("inputExtra");
            createNotificationChannel();
            Intent notificationIntent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Please scan the Qr code or NFC")
                    .setContentText(input)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .build();

            // Issue the notification.
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(555, notification);
        }


        /*  }*/


/*        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);*/


/*        if(intent.getAction()!=null){
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {


                Intent serviceIntent = new Intent(context, NFCAlarmService.class);
                context.startService(serviceIntent);

            } else {
                Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
            }

        }*/


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
}
