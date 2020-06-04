package com.myapp.nfcapplication;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Padmaja.B on 20/12/2018.
 */

public class DrawerFragment extends Fragment implements View.OnClickListener {

    private static String TAG = DrawerFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private View containerView;
    private View layout;
    private TextView txtLoginUser;
    private AppCompatActivity appCompatActivity;

    private IntentFilter mIntentFilter;
    private String userName = "";
    ImageView fText;
    TextView txtUserName,txtPhoneNumber,txtRegDate,btnLogOut,tvAppVersion;
    SharedPreferences prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        appCompatActivity = (AppCompatActivity) getActivity();


        loadFormControls();

        return layout;
    }

    public void loadFormControls() {

        fText = (ImageView) layout.findViewById(R.id.fText);
        txtUserName = (TextView) layout.findViewById(R.id.txtUserName);
        txtPhoneNumber = (TextView) layout.findViewById(R.id.txtPhoneNumber);
        txtRegDate = (TextView) layout.findViewById(R.id.txtRegDate);

        btnLogOut = (TextView) layout.findViewById(R.id.btnLogOut);
        tvAppVersion = (TextView) layout.findViewById(R.id.tvAppVersion);

        prefs = getActivity().getSharedPreferences("CordonOff", MODE_PRIVATE);
        txtUserName.setText(prefs.getString(KeyValues.NAME_OF_THE_PERSON, ""));
        txtPhoneNumber.setText(prefs.getString(KeyValues.MOBILE_ONE, ""));
        txtRegDate.setText(prefs.getString(KeyValues.CREATED_DATE, ""));

        tvAppVersion.setText("" + BuildConfig.VERSION_NAME);


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                editor.putInt(KeyValues.IS_LOGIN, 0);
                editor.apply();
                startActivity(new Intent(getActivity(), UserLoginActivity.class));
                getActivity().finish();

            }
        });

        TextDrawable drawable = TextDrawable.builder().buildRound(txtUserName.getText().toString().substring(0, 1), Color.parseColor("#00FF00"));

        fText.setImageDrawable(drawable);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }


}