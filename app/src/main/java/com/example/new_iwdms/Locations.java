package com.example.new_iwdms;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Locations extends AppCompatActivity {
    private static final String TAG = "LocationFragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private boolean locationFetched;
    private TextView latitudeTextView, longitudeTextView, addressTextView;
    private Button btnGetLocation, btnProceedNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        addressTextView = findViewById(R.id.address);
        btnGetLocation = findViewById(R.id.buttonFetchLocation);
        btnProceedNext = findViewById(R.id.btnproceed);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
            }
        });

        btnProceedNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationFetched) {
                    Intent intent = new Intent(Locations.this, NavigationPage.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Locations.this, "Please get location first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                double latitude = lastKnownLocation.getLatitude();
                                double longitude = lastKnownLocation.getLongitude();
                                locationFetched = true; // Update flag

                                // Save latitude and longitude in SharedPreferences
                                saveLocationToPreferences(latitude, longitude);

                                if (Geocoder.isPresent()) {
                                    Geocoder geocoder = new Geocoder(Locations.this, Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        if (addresses != null && !addresses.isEmpty()) {
                                            Address address = addresses.get(0);
                                            StringBuilder addressString = new StringBuilder();

                                            if (address.getThoroughfare() != null) {
                                                addressString.append(address.getThoroughfare()).append(", ");
                                            }
                                            if (address.getLocality() != null) {
                                                addressString.append(address.getLocality()).append(", ");
                                            }
                                            if (address.getAdminArea() != null) {
                                                addressString.append(address.getAdminArea()).append(", ");
                                            }
                                            if (address.getCountryName() != null) {
                                                addressString.append(address.getCountryName());
                                            }

                                            latitudeTextView.setText("Latitude: " + latitude);
                                            longitudeTextView.setText("Longitude: " + longitude);
                                            addressTextView.setText("Address: " + addressString.toString());
                                        } else {
                                            latitudeTextView.setText("Latitude: " + latitude);
                                            longitudeTextView.setText("Lng: " + longitude);
                                            addressTextView.setText("Address: Not available");
                                        }
                                    } catch (IOException e) {
                                        Log.e(TAG, "Geocoder IOException: " + e.getMessage(), e);
                                        latitudeTextView.setText("Latitude: " + latitude);
                                        longitudeTextView.setText("Longitude: " + longitude);
                                        addressTextView.setText("Address: Error");
                                    }
                                } else {
                                    Log.e(TAG, "Geocoder service not available.");
                                    latitudeTextView.setText("Latitude: " + latitude);
                                    longitudeTextView.setText("Longitude: " + longitude);
                                    addressTextView.setText("Geocoder service not available.");
                                }
                            } else {
                                latitudeTextView.setText("Location not available");
                                longitudeTextView.setText("");
                                addressTextView.setText("");
                                locationFetched = false; // Ensure flag is updated
                            }
                        } else {
                            Log.d(TAG, "Current location is null or location task failed.");
                            Log.e(TAG, "Exception: ", task.getException());
                            latitudeTextView.setText("Unable to get location");
                            longitudeTextView.setText("");
                            addressTextView.setText("");
                            locationFetched = false; // Ensure flag is updated
                        }
                    }
                });
            } else {
                Log.d(TAG, "Location permission not granted.");
                latitudeTextView.setText("Location permission not granted.");
                longitudeTextView.setText("");
                addressTextView.setText("");
                locationFetched = false; // Ensure flag is updated
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security Exception: " + e.getMessage(), e);
            latitudeTextView.setText("Security Exception: " + e.getMessage());
            longitudeTextView.setText("");
            addressTextView.setText("");
            locationFetched = false; // Ensure flag is updated
        }
    }

    private void saveLocationToPreferences(double latitude, double longitude) {
        SharedPreferences sharedPref = getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Latitude", String.valueOf(latitude));
        editor.putString("Longitude", String.valueOf(longitude));
        editor.apply();
    }
}
