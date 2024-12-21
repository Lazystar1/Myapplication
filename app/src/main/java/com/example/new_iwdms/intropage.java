package com.example.new_iwdms;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class intropage extends AppCompatActivity {
    private static final int FADE_DURATION = 5000; // Duration of image animations in milliseconds
   // Duration of image animations in milliseconds
    private static final long SLOW_DEVICE_THRESHOLD_MS = 2000; //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyThemeBasedOnMode();
        setContentView(R.layout.activity_intropage);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Load the custom animation
        Animation fadeInFromBottom = AnimationUtils.loadAnimation(this, R.anim.fade_in_from_bottom);

        // Apply the animation to all views
        View progressBar = findViewById(R.id.progressBar);
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);

        // Initially hide views and then show them with animation
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);

        // Measure device performance
        long startTime = System.currentTimeMillis();

        // Show the loader animation
        if (isNetworkConnected()) {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                progressBar.setVisibility(View.GONE);

                // Start fade-in from bottom animations
                imageView.startAnimation(fadeInFromBottom);
                textView.startAnimation(fadeInFromBottom);
                textView2.startAnimation(fadeInFromBottom);
                textView3.startAnimation(fadeInFromBottom);

                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.VISIBLE);

                // Redirect to LoginActivity after animations
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(intropage.this, login.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish(); // Finish this activity to prevent returning to it
                }, FADE_DURATION); // Delay to match the duration of the animations

                // Measure time taken and show custom dialog if device is slow
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > SLOW_DEVICE_THRESHOLD_MS) {
                    showCustomSlowDeviceDialog();
                }

            }, 1000); // Short delay before starting the animations, to allow progressBar to be seen briefly
        } else {
            // Show an alert dialog if there is no internet connection
            showNoInternetDialog();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK", (dialog, which) -> finish()) // Close the activity if the user clicks "OK"
                .setCancelable(false) // Prevent the dialog from being canceled by clicking outside
                .show();
    }

    private void showCustomSlowDeviceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Performance Notice")
                .setMessage("The device seems to be running slowly. This may affect performance.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()) // Close the dialog if the user clicks "OK"
                .setCancelable(true) // Allow the dialog to be canceled by clicking outside
                .show();
    }

    private void applyThemeBasedOnMode() {
        boolean isNightMode = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (isNightMode) {
            setTheme(R.style.Theme_NavigationPage_Night);
        } else {
            setTheme(R.style.Theme_NavigationPage);
        }
    }
}
