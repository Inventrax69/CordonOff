package com.myapp.nfcapplication;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class FAQActivity extends AppCompatActivity {

    Toolbar toolBar;
    ActionBar actionBar;
    DrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        toolBar = findViewById(R.id.toolBar);

        toolBar.setTitleTextColor(Color.BLACK);

        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();

        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolBar);



    }
}
