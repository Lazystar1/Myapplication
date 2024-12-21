package com.example.new_iwdms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestingAPi extends AppCompatActivity {
    private static final String API_URL = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/api/Tank/GetCityDetails";
    private EditText etTankId, etCityId, etCityName, etTankName, etDescription, etStatusCode, etStatus, etAddUser, etModUser, etStatementType;
    private Button btnFetchData;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_api); // Make sure this matches your layout file name
        // Initialize the views
        etTankId = findViewById(R.id.et_tankid);
        etCityId = findViewById(R.id.et_cityid);
        etCityName = findViewById(R.id.et_cityname);
        etTankName = findViewById(R.id.et_tankname);
        etDescription = findViewById(R.id.et_description);
        etStatusCode = findViewById(R.id.et_statuscode);
        etStatus = findViewById(R.id.et_status);
        etAddUser = findViewById(R.id.et_adduser);
        etModUser = findViewById(R.id.et_moduser);
        etStatementType = findViewById(R.id.et_statementtype);
        btnFetchData = findViewById(R.id.btn_fetch_data);

        // Set onClick listener for the button
        btnFetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new GetDetails().execute();
                } else {
                    Toast.makeText(TestingAPi.this, "No internet connection available", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private class GetDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Parse the JSON response
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    // Extract values from JSON object
                    String tankid = jsonObject.getString("tankid");
                    String cityId = jsonObject.getString("cityid");
                    String cityName = jsonObject.getString("cityName");
                    String tankName = jsonObject.optString("tankName", "N/A");
                    String description = jsonObject.getString("descreption");
                    String statusCode = jsonObject.getString("statuscode");
                    String status = jsonObject.getString("status");
                    String addUser = jsonObject.getString("adduser");
                    String modUser = jsonObject.getString("moduser");
                    String statementType = jsonObject.optString("statementtype", "N/A");

                    // Set the extracted values to EditText fields
                    etTankId.setText(tankid);
                    etCityId.setText(cityId);
                    etCityName.setText(cityName);
                    etTankName.setText(tankName);
                    etDescription.setText(description);
                    etStatusCode.setText(statusCode);
                    etStatus.setText(status);
                    etAddUser.setText(addUser);
                    etModUser.setText(modUser);
                    etStatementType.setText(statementType);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(TestingAPi.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
