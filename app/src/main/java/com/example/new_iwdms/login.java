package com.example.new_iwdms;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class login extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Animation shakeAnimation;
    private TextView errorName, errorUsername, errorEmail, errorPassword;
    private static final String API_URL = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/API/LoginApi/Loginauth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        applyThemeBasedOnMode();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        checkAndRequestPermissions();
        isNetworkConnected();
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        errorUsername = findViewById(R.id.errorUsernameName);
        errorPassword = findViewById(R.id.erroropassword);

        // Load shake animation


        buttonLogin.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                if (validateInputs()) {
                     Login();
                }
            } else {
                showNoInternetDialog();
            }
        });

    }
    
    private void Login() {

// Initialize and show the SpotsDialog
        final SpotsDialog dialog = (SpotsDialog) new SpotsDialog.Builder()
                .setContext(this) // Pass the context here
                .setMessage("Logging in please wait...") // Optional, you can set your custom message
                .setCancelable(false) // Optional, if you want the dialog to be non-cancelable
                .build();
        dialog.show(); // Show the dialog

        API_TEMP apiTemp = new API_TEMP();
        String TextUsername = editTextUsername.getText().toString();
        String TextPassword = editTextPassword.getText().toString();

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("Username", TextUsername);
        urlParams.put("Password", TextPassword);

        //String urlParams = "Username=" + TextUsername + "&Password=" + TextPassword;
        apiTemp.executeGetRequestonp(apiTemp.baseUrl,apiTemp.getlogin, urlParams, new API_TEMP.ApiCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (!response.equals("null")) {
                    // Save username in SharedPreferences
                    JSONObject jsonResponse = new JSONObject(response);

                    // Extract userId from the response
                    int userId = jsonResponse.getInt("userId");
                    String Mobile = jsonResponse.getString("userMobileNo");

                    // Save userId in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userId", userId); // Save userId as an integer
                    editor.putString("userMobileNo", Mobile); // Save userId as an integer
                    editor.putString("Username", TextUsername); // Save Username
                    editor.apply(); // Save changes

                    // Redirect to Locations activity
                    Intent intent = new Intent(login.this, Locations.class);
                    startActivity(intent);
                    finish(); // Optional: finish the current activity if you don't want it in the back stack
                } else if (response.equals("null")) {
                    // Show alert dialog for incorrect username/password

                    showAlert("Invalid credentials", "Please check your username and password.", android.R.drawable.ic_delete);
                } else {
                    // Handle other cases (e.g., unexpected response)
                    showAlert("Error", "An unexpected error occurred. Please try again.", android.R.drawable.ic_dialog_info);
                }
            }

            @Override
            public void onError(String error) {
                // Show alert dialog for error
                showAlert("Error", "An error occurred: " + error, android.R.drawable.ic_dialog_alert);
            }
        });

    }
//    private class Login extends AsyncTask<Void, Void, String> {
//        private AlertDialog dialog = null;
//        @Override
//        protected String doInBackground(Void... params) {
//            StringBuilder result = new StringBuilder();
//            HttpURLConnection connection = null;
//            BufferedReader reader = null;
//
//
//            String TextUsername = editTextUsername.getText().toString();
//            String TextPassword = editTextPassword.getText().toString();
//
//            try {
//                // Construct the URL string without encoding
//                String urlString = API_URL + "?Username=" + TextUsername + "&Password=" + TextPassword;
//
//                URL url = new URL(urlString);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Error: " + e.getMessage();
//            } finally {
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (connection != null) {
//                    connection.disconnect();
//                }
//            }
//            return result.toString();
//        }
//
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (result.equals("200")) {
//                // Save username in SharedPreferences
//                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("Username", editTextUsername.getText().toString());
//                editor.apply();
//
//                // Redirect to Locations activity
//                Intent intent = new Intent(login.this, Locations.class);
//                startActivity(intent);
//            } else if (result.equals("202")) {
//                // Show alert dialog for incorrect username/password
//                showAlert("Invalid credentials", "Please check your username and password.", android.R.drawable.ic_delete);
//            } else {
//                // Handle other cases (e.g., unexpected response)
//                showAlert("Error", "An unexpected error occurred. Please try again.", android.R.drawable.ic_dialog_info);
//            }
//
//        }
//    }


    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        // Check if all permissions are granted
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        // Request permissions if not all are granted
        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Handle the results of the permission request here
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // Permission was denied, you can handle it here
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private boolean validateInputs() {
        errorUsername.setVisibility(View.GONE);
        errorPassword.setVisibility(View.GONE);

        boolean isValid = true;

        if (editTextUsername.getText().toString().trim().isEmpty()) {
            isValid = false;
            errorUsername.setText("Username is required");
            errorUsername.setVisibility(View.VISIBLE);
            startShakeAnimation(editTextUsername);
        }

        if (editTextPassword.getText().toString().trim().isEmpty()) {
            isValid = false;
            errorPassword.setText("Password is required");
            errorPassword.setVisibility(View.VISIBLE);
            startShakeAnimation(editTextPassword);
        }

        return isValid;
    }

    private void startShakeAnimation(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        view.startAnimation(shake);
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

    private void applyThemeBasedOnMode() {
        boolean isNightMode = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (isNightMode) {
            setTheme(R.style.Theme_NavigationPage_Night);
        } else {
            setTheme(R.style.Theme_NavigationPage);
        }
    }
    private void showAlert(String title, String message, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}