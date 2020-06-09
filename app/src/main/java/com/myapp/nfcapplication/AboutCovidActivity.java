package com.myapp.nfcapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutCovidActivity extends AppCompatActivity {

    Toolbar toolBar;
    ActionBar actionBar;
    DrawerFragment drawerFragment;
    SharedPreferences prefs;
    BottomNavigationView navigation;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

            Intent login = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(login);
            finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_covid);

        toolBar = findViewById(R.id.toolBar);

        toolBar.setTitleTextColor(Color.BLACK);

        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();

        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolBar);

        prefs = getSharedPreferences("CordonOff", MODE_PRIVATE);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);       // removes bottom navigation bar animation
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_sync_info:
                break;

            case R.id.action_options:
                break;


            case R.id.action_multilang:
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    if (getApplicationContext() instanceof UserActivity) {

                    } else {
                        startActivity(new Intent(AboutCovidActivity.this, UserActivity.class));
                        finish();
                    }
                    return true;

                case R.id.aboutcovid:
/*                    if (getApplicationContext() instanceof AboutCovidActivity) {

                    } else {
                        startActivity(new Intent(AboutCovidActivity.this, AboutCovidActivity.class));
                        finish();
                    }*/
                    return true;

                case R.id.aboutq:
                    if (getApplicationContext() instanceof AboutQuartineActivity) {

                    } else {
                        startActivity(new Intent(AboutCovidActivity.this, AboutQuartineActivity.class));
                        finish();
                    }
                    return true;

                case R.id.Location:
                    if( !prefs.getString(KeyValues.LATITUDE, "").equals("") && !prefs.getString(KeyValues.LONGITUDE, "").equals("")){
                        startActivity(new Intent(AboutCovidActivity.this, UserMapActivity.class));
                        finish();
                    }else{

                        Toast.makeText(AboutCovidActivity.this, "Home Location not yet set please set it.", Toast.LENGTH_SHORT).show();
                    }
                    return true;

            }
            return false;
        }
    };
}
