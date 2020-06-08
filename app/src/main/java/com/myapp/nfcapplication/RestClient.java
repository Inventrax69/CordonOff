package com.myapp.nfcapplication;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RestClient {

    //public static final String BASE_URL = "http://192.168.1.15/FalconWMSCore_Endpoint/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        try {
            retrofit = null;
            if (retrofit == null) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100,TimeUnit.SECONDS).build();

                retrofit = new Retrofit.Builder()
                       // .baseUrl(ServiceURL.getServiceUrl()).client(client)
                       //.baseUrl("http://103.210.73.73/api/").client(client)
                       //.baseUrl("http://192.168.1.36/Cordonoff/").client(client)
                       //.baseUrl("http://192.168.1.143/Cordonoff/").client(client)
                        .baseUrl("http://cordonoff.in/api/").client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        } catch (Exception ex) {
            Log.d("Exceptionerror", ex.toString());
        }
        return retrofit;
    }

}