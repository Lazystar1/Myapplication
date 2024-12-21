package com.example.new_iwdms;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Schedules extends Fragment {
    private static final String API_URL = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/API/";
    private static final String API_URLmobnum ="https://test.seminalsoftwarepvt.in/VijaypuraMiddleware/api/Water/updateMobileonuserid?";
    private Handler handler;
    private Runnable updateTimeRunnable, updateTimerRunnable;
    // private EditText etStartTime, etDuration;
    private EditText etStartTime, etEndTime;
    private TextView tvDuration;
    private long startTime;
    private boolean isTimerRunning = false;
    private Spinner valvemenSpinner,valveIdspinner;

    public Schedules() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Schedule");
        }
        loadschedulevalIdSpinner();
        loadschedulevalvemenSpinner();
        // Check internet connection
        if (!isNetworkConnected()) {
            showNoInternetDialog();
            return inflater.inflate(R.layout.fragment_schedules, container, false);
        }

        View view = inflater.inflate(R.layout.fragment_schedules, container, false);

        Spinner spnAssignedValves = view.findViewById(R.id.spnAssignedValves);
        Spinner spnAssignedValvesmen = view.findViewById(R.id.spnAssignedValvesmen);

        // Ensure the context is valid
        if (getContext() != null) {
            ArrayAdapter<CharSequence> adapterCodes = ArrayAdapter.createFromResource(getContext(),
                    R.array.spinner_codes, android.R.layout.simple_spinner_item);
            adapterCodes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAssignedValves.setAdapter(adapterCodes);

            ArrayAdapter<CharSequence> adapterNames = ArrayAdapter.createFromResource(getContext(),
                    R.array.spinner_names, android.R.layout.simple_spinner_item);
            adapterNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAssignedValvesmen.setAdapter(adapterNames);
        }

        // Initialize views
        etStartTime = view.findViewById(R.id.et_start_time);
        etEndTime = view.findViewById(R.id.et_duration);
        tvDuration = view.findViewById(R.id.tv_duration);
        Button btnStart = view.findViewById(R.id.btn_start);
        Button btnEnd = view.findViewById(R.id.btn_end);
        Button btnsubmit = view.findViewById(R.id.btnsubmit);
        valvemenSpinner = view.findViewById(R.id.spnAssignedValvesmen);
        valveIdspinner = view.findViewById(R.id.spnAssignedValves);

        // Initialize Handler
        handler = new Handler();

        // Runnable to update the current time every second
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // Update the current time in the EditText
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                String currentTime = sdf.format(Calendar.getInstance().getTime());
                etStartTime.setText(currentTime);

                // Repeat this runnable code block every second
                handler.postDelayed(this, 1000);
            }
        };

        // Set up Start button
        btnStart.setOnClickListener(v -> {
            if (!isTimerRunning) {
                // Start updating current time
                handler.post(updateTimeRunnable);

                // Record the start time
                startTime = Calendar.getInstance().getTimeInMillis();
                isTimerRunning = true;
            }
        });

        // Set up End button
        btnEnd.setOnClickListener(v -> {
            if (isTimerRunning) {
                // Stop updating current time
                handler.removeCallbacks(updateTimeRunnable);

                // Record the end time
                long endTime = Calendar.getInstance().getTimeInMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                String endTimeFormatted = sdf.format(Calendar.getInstance().getTime());
                etEndTime.setText(endTimeFormatted);

                // Calculate the duration
                long durationMillis = endTime - startTime;
                long durationSeconds = durationMillis / 1000;
                long minutes = durationSeconds / 60;
                long seconds = durationSeconds % 60;
                String durationFormatted = String.format("%d min %d sec", minutes, seconds);

                // Show the duration TextView and set its text
                tvDuration.setText(durationFormatted);
                tvDuration.setVisibility(View.VISIBLE);

                // Stop the timer
                isTimerRunning = false;
            }
        });

        btnsubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                try {
                    InsertData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
    private boolean validateInputs() {
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String selectedValveId = valveIdspinner.getSelectedItem().toString();
        String selectedValveMen = valvemenSpinner.getSelectedItem().toString();

        // Validate Valve ID
        if (selectedValveId.equals("Select")) {
            showValidationDialog("Validation Error", "Please select a valid Valve ID.");
            return false;
        }

        // Validate Valve Men
        if (selectedValveMen.equals("Select")) {
            showValidationDialog("Validation Error", "Please select a valid Valve Men.");
            return false;
        }

        // Validate Start Time
        if (startTime.isEmpty()) {
            showValidationDialog("Validation Error", "Start time cannot be empty.");
            return false;
        }

        // Validate End Time
        if (endTime.isEmpty()) {
            showValidationDialog("Validation Error", "End time cannot be empty.");
            return false;
        }



        return true;
    }

    // Validation popup dialog
    private void showValidationDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Close the fragment or navigate to another screen
                    requireActivity().finish(); // Or use other navigation logic
                })
                .show();
    }


    private void loadschedulevalIdSpinner() {
        API_TEMP apiTemp = new API_TEMP();

        // Execute the GET request using the API_TEMP class
        apiTemp.executeGetRequest(apiTemp.baseUrl,apiTemp.getMethod, new API_TEMP.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // Parse the JSON response and set it to the Spinner
                List<String> valveIds = parseValveIds(response);

                // Add "Select" as the first item
                valveIds.add(0, "Select");

                // Create an ArrayAdapter using the valveIds list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, valveIds);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Set the adapter to the Spinner
                valveIdspinner.setAdapter(adapter);

                // Optionally set "Select" as the default selected item
                valveIdspinner.setSelection(0);

                valveIdspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected item
                        String selectedValveId = parent.getItemAtPosition(position).toString();
                        // Handle the selected Valve ID if needed
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle case when no item is selected, if needed
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to retrieve data: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    // Method to parse the JSON response into a List of valve IDs
    private List<String> parseValveIds(String jsonResponse) {
        List<String> valveIds = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject valveObject = jsonArray.getJSONObject(i);
                String valveId = valveObject.getString("valveid");
                valveIds.add(valveId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return valveIds;
    }

    private void loadschedulevalvemenSpinner() {
        API_TEMP apiTemp = new API_TEMP();

        // Execute the GET request using the API_TEMP class
        apiTemp.executeGetRequest(apiTemp.baseUrl,apiTemp.GetValvemen, new API_TEMP.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // Parse the JSON response and set it to the Spinner
                List<String> valvemens = parsevalvemenSpinner(response);

                // Add "Select" as the first item
                valvemens.add(0, "Select");

                // Create an ArrayAdapter using the valveIds list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, valvemens);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Set the adapter to the Spinner
                valvemenSpinner.setAdapter(adapter);

                // Optionally set "Select" as the default selected item
                valvemenSpinner.setSelection(0);

                valvemenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected item
                        String selectedValveId = parent.getItemAtPosition(position).toString();
                        // Handle the selected Valve ID if needed
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle case when no item is selected, if needed
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to retrieve data: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    // Method to parse the JSON response into a List of valve IDs
    private List<String> parsevalvemenSpinner(String jsonResponse) {
        List<String> valveIds = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject valveObject = jsonArray.getJSONObject(i);
                String valvemen = valveObject.getString("valve_men");
                valveIds.add(valvemen);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return valveIds;
    }


    private void InsertData() {
        API_TEMP apiTemp = new API_TEMP();
        String selectedValveId = valveIdspinner.getSelectedItem().toString();
        String selectedValveMen = valvemenSpinner.getSelectedItem().toString();

        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();
        String duration = tvDuration.getText().toString();

        SharedPreferences sharedPref = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
        String latitude = sharedPref.getString("Latitude", "default_latitude");
        String longitude = sharedPref.getString("Longitude", "default_longitude");

        String urlParams = "Valve_Unique_Id=" + selectedValveId +
                "&Valve_men=" +selectedValveMen+
                "&start_Time=" + startTime +
                "&end_Time=" + endTime +
                "&Duration=" + duration +
                "&Latitude=" + latitude +
                "&Longitude=" + longitude;

        apiTemp.executePostRequest(apiTemp.baseUrl,apiTemp.postscheduleMethod, urlParams, new API_TEMP.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getContext(), "Inserted Successfully" ,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Failed To Insert " , Toast.LENGTH_LONG).show();
            }
        });
//        new Schedules.PostDataTask().execute(url);
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
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
        }
    }
}