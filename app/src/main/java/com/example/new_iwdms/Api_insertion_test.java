package com.example.new_iwdms;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Api_insertion_test extends AppCompatActivity {
    private EditText etOverheadTankId, etCityId, etTankId, etConnectionType, etDemand, etTapType, etValveOpenTime, etValveCloseTime, etAvgConsumption, etDescription, etStatementType;
    private Button btnInsert;

    private static final String API_URL = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/api/Overhead/InsertUpdateOverTankDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_insertion_test);
        // Initialize the views
        etOverheadTankId = findViewById(R.id.et_overheadtankid);
        etCityId = findViewById(R.id.et_cityid);
        etTankId = findViewById(R.id.et_tankid);
        etConnectionType = findViewById(R.id.et_connectiontype);
        etDemand = findViewById(R.id.et_demand);
        etTapType = findViewById(R.id.et_taptype);
        etValveOpenTime = findViewById(R.id.et_valveopentime);
        etValveCloseTime = findViewById(R.id.et_valveclosetime);
        etAvgConsumption = findViewById(R.id.et_avgconsumption);
        etDescription = findViewById(R.id.et_description);
        etStatementType = findViewById(R.id.et_statementtype);
        btnInsert = findViewById(R.id.btn_insert);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });
    }

    private void insertData() {
        String overheadTankId = etOverheadTankId.getText().toString();
        String cityId = etCityId.getText().toString();
        String tankId = etTankId.getText().toString();
        String connectionType = etConnectionType.getText().toString();
        String demand = etDemand.getText().toString();
        String tapType = etTapType.getText().toString();
        String valveOpenTime = etValveOpenTime.getText().toString();
        String valveCloseTime = etValveCloseTime.getText().toString();
        String avgConsumption = etAvgConsumption.getText().toString();
        String description = etDescription.getText().toString();
        String statementType = etStatementType.getText().toString();

        String url = API_URL + "?overheadTankid=" + overheadTankId +
                "&cityid=" + cityId +
                "&tankid=" + tankId +
                "&connectionType=" + connectionType +
                "&demand=" + demand +
                "&tapType=" + tapType +
                "&valveopenTime=" + valveOpenTime +
                "&valvecloseTime=" + valveCloseTime +
                "&avgConsumption=" + avgConsumption +
                "&descreption=" + description +
                "&statementtype=" + statementType;

        new PostDataTask().execute(url);
    }

    private class PostDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);

                OutputStream os = urlConnection.getOutputStream();
                os.write(new byte[0]); // Write an empty body, as we use URL parameters.
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Data inserted successfully";
                } else {
                    return "Failed to insert data: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(Api_insertion_test.this, result, Toast.LENGTH_LONG).show();
        }
    }
    }
