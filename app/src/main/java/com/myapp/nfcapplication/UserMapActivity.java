package com.myapp.nfcapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    int isHomeQuarantine;
    SharedPreferences prefs;
    String homeLat,homeLong;
    MapFragment mapFragment;
    Toolbar toolBar;
    ActionBar actionBar;
    DrawerFragment drawerFragment;
    TextView txtLat,txtLong,txtLocationRange;

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
        setContentView(R.layout.activity_user_map);

        toolBar = findViewById(R.id.toolBar);

        toolBar.setTitleTextColor(Color.BLACK);

        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();

        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolBar);


        txtLat = (TextView)findViewById(R.id.txtLat);
        txtLong = (TextView)findViewById(R.id.txtLong);
        txtLocationRange = (TextView)findViewById(R.id.txtLocationRange);

        prefs = getSharedPreferences("CordonOff", MODE_PRIVATE);
        isHomeQuarantine = prefs.getInt(KeyValues.IS_HOME_QUARANTINE, 0);
        homeLat = prefs.getString(KeyValues.LATITUDE, "");
        homeLong = prefs.getString(KeyValues.LONGITUDE, "");

        txtLat.setText(homeLat);
        txtLong.setText(homeLong);


        txtLocationRange.setText("Location Range Allowed : "+prefs.getInt(KeyValues.CONFIGURED_GEO_LOCATION,50)+" Meters");

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);       // removes bottom navigation bar animation
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (isHomeQuarantine == 1) {

            double lat = Double.parseDouble(homeLat);
            double lang = Double.parseDouble(homeLong);

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lang))
                    .title(""));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lang), 15));

            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());

            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lang))      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
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
                        startActivity(new Intent(UserMapActivity.this, UserActivity.class));
                        finish();
                    }
                    return true;

                case R.id.aboutcovid:
                    if (getApplicationContext() instanceof AboutCovidActivity) {

                    } else {
                        startActivity(new Intent(UserMapActivity.this, AboutCovidActivity.class));
                        finish();
                    }
                    return true;

                case R.id.aboutq:
                    if (getApplicationContext() instanceof AboutQuartineActivity) {

                    } else {
                        startActivity(new Intent(UserMapActivity.this, AboutQuartineActivity.class));
                        finish();
                    }
                    return true;

                case R.id.Location:
/*                    if( !prefs.getString(KeyValues.LATITUDE, "").equals("") && !prefs.getString(KeyValues.LONGITUDE, "").equals("")){
                        startActivity(new Intent(AboutCovidActivity.this, UserMapActivity.class));
                        finish();
                    }else{

                        Toast.makeText(AboutCovidActivity.this, "Home Location not yet set please set it.", Toast.LENGTH_SHORT).show();
                    }*/
                    return true;

            }
            return false;
        }
    };
}
