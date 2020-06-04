package com.myapp.nfcapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Padmaja on 02/08/2019.
 */

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;
    public static final int MULTIPLE_PERMISSIONS = 15;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // when this activity is about to be launch we need to check if its openened before or not

        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(mainActivity);
            finish();
        }

        setContentView(R.layout.activity_intro);

        loadFormControls();

    }

    public void loadFormControls() {

        try {


            // ini views
            btnNext = (Button) findViewById(R.id.btn_next);
            btnGetStarted = (Button) findViewById(R.id.btn_get_started);
            tabIndicator = (TabLayout) findViewById(R.id.tab_indicator);
            btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
            tvSkip = (TextView) findViewById(R.id.tv_skip);

            // fill list screen
            final List<ScreenItem> mList = new ArrayList<>();
            mList.add(new ScreenItem("Order Booking", "Cordonoff is a Digital Surveillance and Geo-Fence Enforcement Application designed to prevent the spread of the coronavirus.", R.drawable.logo));
            mList.add(new ScreenItem("Express Delivery", "Cordonoff acts as your guide towards a safer self-quarantine period by using geo-fence monitoring to ensure you pass the window of COVID-19 transmission risk. \n Simply, 1) Install the app \n 2. Register with your details \n 3. Wear the NFC enabled tracking band \n 4. Map your self-quarantine location \n Sit tight and let Cordonoff handle your safety during the Isolation ", R.drawable.logo));
            // mList.add(new ScreenItem("Order Status", "We are getting India's favourite furniture brand closer to you with just a click away!", R.drawable.logo));

            // setup viewpager
            screenPager = (ViewPager) findViewById(R.id.screen_viewpager);
            introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
            screenPager.setAdapter(introViewPagerAdapter);

            // setup tablayout with viewpager
            tabIndicator.setupWithViewPager(screenPager);

            // next button click Listner
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    position = screenPager.getCurrentItem();
                    if (position < mList.size()) {
                        position++;
                        screenPager.setCurrentItem(position);
                    }

                    if (position == mList.size() - 1) { // when we reach to the last screen
                        // TODO : show the GET STARTED Button and hide the indicator and the next button
                        loaddLastScreen();
                    }

                }
            });

            // tab layout add change listener


            tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    if (tab.getPosition() == mList.size() - 1) {
                        loaddLastScreen();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


            // Get Started button click listener

            btnGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //navigation transition
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                    //open splash activity
                    Intent login = new Intent(getApplicationContext(), TermsAndConditionActivity.class);
                    startActivity(login);
                    // also we need to save a boolean value to storage so next time when the user run the app
                    // we could know that he is already checked the intro screen activity
                    // i'm going to use shared preferences to that process
                    savePrefsData();
                    finish();


                }
            });

            // skip button click listener

            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screenPager.setCurrentItem(mList.size());
                }
            });
        } catch (Exception ex) {
            // Toast.makeText(this,  ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean restorePrefData() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("CordonOff", MODE_PRIVATE);
        boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpened", false);
        return isIntroActivityOpnendBefore;

    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("CordonOff", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();

    }


    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);

    }
}
