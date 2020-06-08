package com.myapp.nfcapplication;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    ImageView fText, fClose;
    TextView txtUserName, txtPhoneNumber, txtRegDate, btnLogOut, tvAppVersion, txtLocation, btnHome, btnAbout;
    SharedPreferences prefs;
    RelativeLayout relMap;


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
        fClose = (ImageView) layout.findViewById(R.id.fClose);
        txtUserName = (TextView) layout.findViewById(R.id.txtUserName);
        txtPhoneNumber = (TextView) layout.findViewById(R.id.txtPhoneNumber);
        txtRegDate = (TextView) layout.findViewById(R.id.txtRegDate);
        txtLocation = (TextView) layout.findViewById(R.id.txtLocation);

        btnHome = (TextView) layout.findViewById(R.id.btnHome);
        btnAbout = (TextView) layout.findViewById(R.id.btnAbout);

        btnLogOut = (TextView) layout.findViewById(R.id.btnLogOut);
        tvAppVersion = (TextView) layout.findViewById(R.id.tvAppVersion);

        relMap = (RelativeLayout) layout.findViewById(R.id.relMap);

        prefs = getActivity().getSharedPreferences("CordonOff", MODE_PRIVATE);
        txtUserName.setText(prefs.getString(KeyValues.NAME_OF_THE_PERSON, ""));
        txtPhoneNumber.setText(prefs.getString(KeyValues.MOBILE_ONE, ""));
        txtRegDate.setText(prefs.getString(KeyValues.CREATED_DATE, ""));
        txtLocation.setText(prefs.getString(KeyValues.LATITUDE, "") + " , " + prefs.getString(KeyValues.LONGITUDE, ""));

        tvAppVersion.setText("" + BuildConfig.VERSION_NAME);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("CordonOff", MODE_PRIVATE).edit();
                editor.putInt(KeyValues.IS_LOGIN, 0);
                editor.apply();
                startActivity(new Intent(getActivity(), UserLoginActivity.class));
                getActivity().finish();

            }
        });


        relMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if( !prefs.getString(KeyValues.LATITUDE, "").equals("") && !prefs.getString(KeyValues.LONGITUDE, "").equals("")){
                   if (getActivity() instanceof UserMapActivity) {
                       mDrawerLayout.closeDrawer(Gravity.LEFT);
                   } else {
                       mDrawerLayout.closeDrawer(Gravity.LEFT);
                       startActivity(new Intent(getActivity(), UserMapActivity.class));
                       getActivity().finish();
                   }
               }else{
                   mDrawerLayout.closeDrawer(Gravity.LEFT);
                   Toast.makeText(getActivity(), "Home Location not yet set please set it.", Toast.LENGTH_SHORT).show();
               }

            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof UserActivity) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(new Intent(getActivity(), UserActivity.class));
                    getActivity().finish();
                }

            }
        });


        layout.findViewById(R.id.btnAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof AboutCovidActivity) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(new Intent(getActivity(), AboutCovidActivity.class));
                    getActivity().finish();
                }
            }
        });

        layout.findViewById(R.id.btnAboutSelf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof AboutQuartineActivity) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(new Intent(getActivity(), AboutQuartineActivity.class));
                    getActivity().finish();
                }
            }
        });

        layout.findViewById(R.id.btnTandC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof TermsAndConditionActivity) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(new Intent(getActivity(), TermsAndConditionActivity.class));
                    getActivity().finish();
                }
            }
        });

/*        layout.findViewById(R.id.btnFAQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FAQActivity) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(new Intent(getActivity(), FAQActivity.class));
                    getActivity().finish();
                }
            }
        });*/


        fClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });


        // TextDrawable drawable = TextDrawable.builder().buildRound(txtUserName.getText().toString().substring(0, 1), Color.parseColor("#00FF00"));
        // fText.setImageDrawable(drawable);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {

        try {
            containerView = getActivity().findViewById(fragmentId);
            mDrawerLayout = drawerLayout;
            mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getActivity().invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    getActivity().invalidateOptionsMenu();
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    toolbar.setAlpha(1 - slideOffset / 2);
                }
            };


            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        } catch (Exception ex) {
            // Logger.Log(DrawerFragment.class.getName(),ex);
            return;
        }

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