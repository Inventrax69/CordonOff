package com.myapp.nfcapplication.Pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerRegistration implements Serializable {

    @SerializedName("CustomerID")
    private int CustomerID;

    @SerializedName("FirstName")
    private String FirstName;

    @SerializedName("LastName")
    private String LastName;

    @SerializedName("AadharNumber")
    private String AadharNumber;

    @SerializedName("MobileNo1")
    private String MobileNo1;

    @SerializedName("MobileNo2")
    private String MobileNo2;

    @SerializedName("AddressLine1")
    private String AddressLine1;

    @SerializedName("AddressLine2")
    private String AddressLine2;

    @SerializedName("PinCode")
    private String PinCode;

    @SerializedName("IsHomeQuarantine")
    private int IsHomeQuarantine;

    @SerializedName("Latitude")
    private String Latitude;

    @SerializedName("Longitude")
    private String Longitude;

    @SerializedName("IsMobileSmartPhone")
    private int IsMobileSmartPhone;

    @SerializedName("IsMobileNFCEnabled")
    private int IsMobileNFCEnabled;

    @SerializedName("AmountPaidThrough")
    private String AmountPaidThrough;

    @SerializedName("Country")
    private String Country;

    @SerializedName("State")
    private String State;

    @SerializedName("City")
    private String City;

    @SerializedName("TravelHistoryID")
    private int TravelHistoryID;

    @SerializedName("VialotionType")
    private String VialotionType;

    @SerializedName("VialotionID")
    private int VialotionID;

    @SerializedName("TravellingToID")
    private int TravellingToID;

    @SerializedName("IsRegistrationCompleted")
    private int IsRegistrationCompleted;

    @SerializedName("VialotionCount")
    private int VialotionCount;

    @SerializedName("GeoVialotionCount")
    private int GeoVialotionCount;

    @SerializedName("CreatedDate")
    private String CreatedDate;

    @SerializedName("Status")
    private String Status;

    @SerializedName("QRCode")
    private String QRCode;



    @SerializedName("GEOVialotion")
    private String GEOVialotionType;


    @SerializedName("IsBandViolation")
    private boolean IsBandViolation;

    @SerializedName("IsGeoViolation")
    private boolean IsGeoViolation;

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getAadharNumber() {
        return AadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        AadharNumber = aadharNumber;
    }

    public String getMobileNo1() {
        return MobileNo1;
    }

    public void setMobileNo1(String mobileNo1) {
        MobileNo1 = mobileNo1;
    }

    public String getMobileNo2() {
        return MobileNo2;
    }

    public void setMobileNo2(String mobileNo2) {
        MobileNo2 = mobileNo2;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        AddressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        AddressLine2 = addressLine2;
    }

    public String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        PinCode = pinCode;
    }

    public int getIsHomeQuarantine() {
        return IsHomeQuarantine;
    }

    public void setIsHomeQuarantine(int isHomeQuarantine) {
        IsHomeQuarantine = isHomeQuarantine;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public int getIsMobileSmartPhone() {
        return IsMobileSmartPhone;
    }

    public void setIsMobileSmartPhone(int isMobileSmartPhone) {
        IsMobileSmartPhone = isMobileSmartPhone;
    }

    public int getIsMobileNFCEnabled() {
        return IsMobileNFCEnabled;
    }

    public void setIsMobileNFCEnabled(int isMobileNFCEnabled) {
        IsMobileNFCEnabled = isMobileNFCEnabled;
    }

    public String getAmountPaidThrough() {
        return AmountPaidThrough;
    }

    public void setAmountPaidThrough(String amountPaidThrough) {
        AmountPaidThrough = amountPaidThrough;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public int getTravelHistoryID() {
        return TravelHistoryID;
    }

    public void setTravelHistoryID(int travelHistoryID) {
        TravelHistoryID = travelHistoryID;
    }

    public String getVialotionType() {
        return VialotionType;
    }

    public void setVialotionType(String vialotionType) {
        VialotionType = vialotionType;
    }

    public int getVialotionID() {
        return VialotionID;
    }

    public void setVialotionID(int vialotionID) {
        VialotionID = vialotionID;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getTravellingToID() {
        return TravellingToID;
    }

    public void setTravellingToID(int travellingToID) {
        TravellingToID = travellingToID;
    }

    public int getIsRegistrationCompleted() {
        return IsRegistrationCompleted;
    }

    public void setIsRegistrationCompleted(int isRegistrationCompleted) {
        IsRegistrationCompleted = isRegistrationCompleted;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public int getVialotionCount() {
        return VialotionCount;
    }

    public void setVialotionCount(int vialotionCount) {
        VialotionCount = vialotionCount;
    }

    public String getGEOVialotionType() {
        return GEOVialotionType;
    }

    public void setGEOVialotionType(String GEOVialotionType) {
        this.GEOVialotionType = GEOVialotionType;
    }


    public boolean isBandViolation() {
        return IsBandViolation;
    }

    public void setBandViolation(boolean bandViolation) {
        IsBandViolation = bandViolation;
    }

    public boolean isGeoViolation() {
        return IsGeoViolation;
    }

    public void setGeoViolation(boolean geoViolation) {
        IsGeoViolation = geoViolation;
    }


    public int getGeoVialotionCount() {
        return GeoVialotionCount;
    }

    public void setGeoVialotionCount(int geoVialotionCount) {
        GeoVialotionCount = geoVialotionCount;
    }
}
