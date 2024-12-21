package com.example.new_iwdms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.util.Date;

public class NavigationPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "NavigationPage";
    private TextView tvheaderusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_page);
        isNetworkConnected();

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navview);
        View headerView = navigationView.getHeaderView(0); // Get the header view
        tvheaderusername = headerView.findViewById(R.id.tvusername);

        if (tvheaderusername == null) {
            Log.e(TAG, "TextView with ID 'tvusername' is not found in the header view. Check the nav_header layout file.");
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String storedUserName = sharedPreferences.getString("Username", "Default User Name");
            Log.d(TAG, "Retrieved Username from SharedPreferences: " + storedUserName);
            tvheaderusername.setText(storedUserName);
        }

        // Set custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.activity_custom_action_bar);
        }

        // Update the time and date in the action bar
        updateDateTime();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            replaceFragment(new Dashboard());
        }
    }

    private void updateDateTime() {
        final TextView dateTimeTextView = findViewById(R.id.action_bar_date_time);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Get current date and time
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                // Update the TextView
                dateTimeTextView.setText(currentDateTimeString);

                // Update every second
                handler.postDelayed(this, 1000);
            }
        };

        // Start the initial update
        handler.post(runnable);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_Dashboard:
                fragment = new Dashboard();
                break;
            case R.id.nav_valvedetails:
                fragment = new ValveDetail();
                break;
//            case R.id.nav_Schedule:
//                fragment = new Schedules();
//                break;
            case R.id.nav_Notifications:
                fragment = new Notifications();
                break;
            case R.id.nav_settings:
                fragment = new Settings();
                break;
            case R.id.nav_logout:
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Clear all preferences or editor.remove("Username");
                editor.apply();
                Intent intent = new Intent(NavigationPage.this, login.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }

        if (fragment != null) {
            Log.d(TAG, "Replacing fragment: " + fragment.getClass().getSimpleName());
            replaceFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

}
