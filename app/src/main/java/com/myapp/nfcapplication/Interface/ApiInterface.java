package com.myapp.nfcapplication.Interface;


import com.myapp.nfcapplication.Pojo.CustomerRegistration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {


    /*@POST("api/ProcessPickRequest")
    Call<String> ProcessPickRequest(@Body PickReqDTO pickReqDTO);*/


    @POST("Customer/CreateCustomer")
    Call<String> CreateCustomer(@Body CustomerRegistration customerRegistration);

    @POST("Customer/CustomerTravelHistory")
    Call<String> CustomerTravelHistory(@Body CustomerRegistration customerRegistration);

    @POST("Customer/GETCustomerData")
    Call<String> GETCustomerData(@Body CustomerRegistration customerRegistration);

    @POST("Customer/SetHomeQuarintine")
    Call<String> SetHomeQuarintine(@Body CustomerRegistration customerRegistration);

    @POST("Customer/SetVialotions")
    Call<String> SetVialotions(@Body CustomerRegistration customerRegistration);

    @POST("Customer/SETCustomerTravellingTo")
    Call<String> SETCustomerTravellingTo(@Body CustomerRegistration customerRegistration);

    @POST("Customer/UpdateQRCode")
    Call<String> UpdateQRCode(@Body CustomerRegistration customerRegistration);


    @GET("Customer/GetCountries")
    Call<String> GetCountries();

    @GET("Customer/GetStatesbasedonCountry/{CountryName}")
    Call<String> GetStatesbasedonCountry(@Query("CountryName") String CountryName);


    @GET("Customer/GetCitybasedonState/{State}")
    Call<String> GetCitybasedonState(@Query("State") String State);





   /* @GET("api/GetPendingItemList/{obdNumberList}")
    Call<List<OBDInfo>> GetPendingItemList(@Query("obdNumberList") String obdNumberList);*/

    @GET("api/GetDataSet/{sqlString}")
    Call<String> GetDataSet(@Query("sqlString") String sqlquery);
}