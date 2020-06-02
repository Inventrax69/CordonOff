package com.myapp.nfcapplication;

import android.content.Context;

import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;


public class MaterialDialogUtils {

    private Context context;
    public String positiveText="Ok";
    public String negativeText="Cancel";

    public MaterialDialogUtils(){

    }

    public static void showUploadSuccessDialog(Context context, String message){
        new CDialog(context).createAlert(message,
                CDConstants.SUCCESS,   // Type of dialog
                CDConstants.LARGE)    //  size of dialog
                .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)     //  Animation for enter/exit
                .setDuration(2000)   // in milliseconds
                .setTextSize(CDConstants.LARGE_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                .show();
    }

    public static void showUploadErrorDialog(Context context, String message){
        new CDialog(context).createAlert(message,
                CDConstants.ERROR,   // Type of dialog
                CDConstants.LARGE)    //  size of dialog
                .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)     //  Animation for enter/exit
                .setDuration(2000)   // in milliseconds
                .setTextSize(CDConstants.LARGE_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                .show();
    }


}
